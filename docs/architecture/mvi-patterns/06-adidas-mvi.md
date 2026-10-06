# 06. adidas MVI

| | |
|---|---|
| Family | Executor decides, transforms apply (the MVIKotlin family) |
| Who decides | `IntentExecutor` (impure), which returns a `Flow` of named, pure `StateTransform`s |
| Platform | Core is KMP (JVM, Apple). `mvi-compose`: JVM, Android, iOS. `mvi-kotest`: JVM |
| Version checked | `1.9.5` (2026-04-21) · 109 ★ · Apache-2.0 · "Initially developed for the adidas CONFIRMED app" |
| Status | **Inspiration** for logging, cancellation and failure containment |

## How it works

```
Intent ──▶ IntentExecutor (impure, async)  ──  Flow<StateTransform>
                                                 │ emit(SetLoading)
                                                 │ emit(SetLoaded(w, f))
                                                 ▼
           Reducer: transforms applied one at a time (scan over a SharedFlow)
                                                 ▼
           StateFlow<State<TView, TSideEffect>>
```

Core types, verified from source:

- `Reducer<TIntent, TState>(coroutineScope, initialState, logger, defaultDispatcher, intentExecutor)`: `state: StateFlow<TState>`, `executeIntent(intent)`.
- `fun interface IntentExecutor<TIntent, TState> { fun executeIntent(intent): Flow<StateTransform<TState>> }`
- `ViewTransform<TView, TSideEffect>` with `mutate(currentState: TView)`, and `SideEffectTransform` with `mutate(sideEffects)`.
- `State<TView, TSideEffect>(val view: TView, val sideEffects: SideEffects<TSideEffect>)`.
- Markers: `UniqueIntent`, `LoggableState`. Host: `MviHost<in TIntent, out TState : LoggableState> { val state; fun execute(intent) }`.
- 1.9.5 adds a DSL inside a host: `intent { reduce { }; postSideEffect(e); reduceInState<T> { } }`.

## Four ideas worth noticing

1. **State changes are named objects.** `SetLoaded(w, f)` is a pure function you can test, and it shows up readably in logs.
2. **Side effects live inside the state** and are consumed once. Reading them empties the list (`getAndSet(emptyList())`). This follows Google's "model events as state" guidance. (PR #51, merged 2026-09-24 and not yet released, reworks it for thread safety.)
3. **`UniqueIntent`.** Starting one cancels earlier running jobs of the same intent class (`cleanIntentJobsOfType`) with `TerminatedIntentException`, so the newest one wins.
4. **One logger, with failures contained.** Every intent, transform and failure is logged. A transform that throws is logged and the previous state is kept.

## Weather example

```kotlin
sealed class WeatherIntent : Intent {
    data class Search(val query: String) : WeatherIntent(), UniqueIntent   // new Search cancels the running one
    data object Retry : WeatherIntent(), UniqueIntent
    data class DeleteHistory(val item: CacheSearchUiModel) : WeatherIntent()
}

object WeatherTransform {
    data class SetLoading(val query: String) : ViewTransform<WeatherUIState, WeatherUIEffect>() {
        override fun mutate(currentState: WeatherUIState) = currentState.copy(query = query, loadState = LoadState.Loading)
    }
    data class SetLoaded(val weather: WeatherUiModel, val forecast: ForecastUiModel) : ViewTransform<WeatherUIState, WeatherUIEffect>() {
        override fun mutate(currentState: WeatherUIState) =
            currentState.copy(loadState = LoadState.Success, weatherUiModel = weather, forecastUiModel = forecast)
    }
    data class SetFailed(val message: String?) : ViewTransform<WeatherUIState, WeatherUIEffect>() {
        override fun mutate(currentState: WeatherUIState) = currentState.copy(loadState = LoadState.Error(message))
    }
    data class AddSideEffect(val effect: WeatherUIEffect) : SideEffectTransform<WeatherUIState, WeatherUIEffect>() {
        override fun mutate(sideEffects: SideEffects<WeatherUIEffect>) = sideEffects.add(effect)
    }
}

class WeatherViewModel(...) : ViewModel(), MviHost<WeatherIntent, State<WeatherUIState, WeatherUIEffect>> {

    private val reducer = Reducer(
        coroutineScope = viewModelScope,
        initialInnerState = WeatherUIState(),
        intentExecutor = this::executeIntent,
    )
    override val state = reducer.state
    override fun execute(intent: WeatherIntent) = reducer.executeIntent(intent)

    private fun executeIntent(intent: WeatherIntent) = when (intent) {
        is WeatherIntent.Search -> executeSearch(intent.query, save = true)
        WeatherIntent.Retry -> executeSearch(reducer.state.value.view.query, save = false)
        is WeatherIntent.DeleteHistory -> executeDelete(intent.item)
    }

    private fun executeSearch(query: String, save: Boolean) = flow {
        validate(query)?.let {
            emit(WeatherTransform.AddSideEffect(WeatherUIEffect.ShowError(it)))
            return@flow
        }
        emit(WeatherTransform.SetLoading(query))
        try {
            val (w, f) = loadWeather(query)
            emit(WeatherTransform.SetLoaded(w, f))
            if (save) insertCacheSearchUseCase(CacheSearch(id = 0, query = query))   // only reached on success
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emit(WeatherTransform.SetFailed(e.message))   // without this, the library logs it and the screen stays on Loading
        }
    }

    private fun executeDelete(item: CacheSearchUiModel) = flow<StateTransform<State<WeatherUIState, WeatherUIEffect>>> {
        try {
            deleteCacheSearchUseCase(CacheSearch(id = item.id, query = item.query))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emit(WeatherTransform.AddSideEffect(WeatherUIEffect.ShowError(e.message ?: "Failed to delete")))
        }
    }
}
```

> The `Reducer` constructor parameter names in this sketch follow the library sample. Check them against the version you use.

## Compose and testing

- `MviContainer(state, onSideEffect, onViewState)` and `MviScreen(...)` use `collectAsStateWithLifecycle()` and drain side effects in `SideEffect { }`.
- `mvi-kotest` is **Kotest only**: `GivenViewModel { WhenIntent(...) { ThenState(...) } }`, plus `TestMviLogger`.

## Compared with ours

| adidas | Ours ([04](04-elm-commands.md)) |
|---|---|
| Executor decides | Reducer decides |
| Named `ViewTransform` classes | Named result **events**, so they're already named. Adding transforms would name each change twice |
| `UniqueIntent` (cancel by intent class) | `launchUnique(key)` (cancel by key) + `requestId` |
| Effects stored inside the state type, consumed once on read | Effects held as a pending queue next to the state, removed after the UI confirms with `effectHandled(id)` |
| Logger + failure containment built in | `onTransition` + `onReducerError` hooks |

## What we take

1. **`UniqueIntent` → `launchUnique(key)`.**
2. **Central logging → `onTransition(event, old, next)`.**
3. **Failure containment → `onReducerError`.** It keeps the old state. We rethrow in debug because a pure reducer throwing is a bug.
4. **`MviHost` (the name too).**
5. **Effects as state (adapted).** We keep the idea but hold the queue in the base, not inside each screen's state type, and remove an effect only after it's handled. See [borrowed-ideas, effect delivery](borrowed-ideas.md#effect-delivery-a-queue-held-as-state).

## References

- Intro: https://adidas.github.io/mvi/docs/intro
- Core concepts: https://adidas.github.io/mvi/docs/core_concepts
- Getting started: https://adidas.github.io/mvi/docs/getting_started
- `Reducer.kt`: https://github.com/adidas/mvi/blob/main/mvi/src/commonMain/kotlin/com/adidas/mvi/Reducer.kt
