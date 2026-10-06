# 07. MVIKotlin

| | |
|---|---|
| Family | Executor decides, reducer applies |
| Who decides | `Executor` (impure). `Reducer` is a pure `State.reduce(Message): State` |
| Platform | KMP: Android, JVM, iOS, watchOS, tvOS, macOS, linuxX64, JS, Wasm |
| Version checked | `4.4.0` (2026-04-25) · ~1.0k ★ · Apache-2.0 |
| Status | **Inspiration.** Also the easiest step from Option A towards a reducer |

## Core API (verified from source)

- `Store<in Intent, out State, out Label>`: `state`, `accept(intent)`, `init()`, `dispose()`, `states(observer)`, `labels(observer)`.
- `storeFactory.create(name, initialState, bootstrapper, executorFactory, reducer)`.
- `CoroutineExecutor<Intent, Action, State, Message, Label>` (from `mvikotlin-extensions-coroutines`): `scope`, `dispatch(message)`, `publish(label)`, `forward(action)`, `state()`. `state()` is current, and `getState` is from v3.
- `fun interface Reducer<State, in Message> { fun State.reduce(msg: Message): State }`.
- `Msg` is only the docs' naming convention. The type parameter is `Message`.
- **Labels** are one-shot outputs: "delivered to all current subscribers and are not cached".
- The executor, reducer and dispatch all run on the **main thread** (`@MainThread`), so messages are applied in order.
- There's no Compose module and no published test module. The README recommends Decompose for navigation and lifecycle.

## Weather example

```kotlin
interface WeatherStore : Store<WeatherStore.Intent, WeatherUIState, WeatherUIEffect> {
    sealed interface Intent {
        data class Search(val query: String) : Intent
        data object Retry : Intent
        data class DeleteHistory(val item: CacheSearchUiModel) : Intent
    }
}

private sealed interface Msg {                                // facts the executor reports
    data class Loading(val query: String) : Msg
    data class Loaded(val weather: WeatherUiModel, val forecast: ForecastUiModel) : Msg
    data class Failed(val message: String?) : Msg
}

private class ExecutorImpl(...) : CoroutineExecutor<WeatherStore.Intent, Nothing, WeatherUIState, Msg, WeatherUIEffect>() {
    private var searchJob: Job? = null

    override fun executeIntent(intent: WeatherStore.Intent) {
        when (intent) {
            is WeatherStore.Intent.Search ->
                validate(intent.query)?.let { publish(WeatherUIEffect.ShowError(it)) }   // Label = one-shot
                    ?: search(intent.query, save = true)
            WeatherStore.Intent.Retry -> search(state().query, save = false)
            is WeatherStore.Intent.DeleteHistory -> scope.launch {
                try {
                    deleteCacheSearchUseCase(CacheSearch(id = intent.item.id, query = intent.item.query))
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    publish(WeatherUIEffect.ShowError(e.message ?: "Failed to delete"))
                }
            }
        }
    }

    private fun search(query: String, save: Boolean) {
        searchJob?.cancel()
        searchJob = scope.launch {
            dispatch(Msg.Loading(query))
            try {
                val (w, f) = loadWeather(query)
                dispatch(Msg.Loaded(w, f))
                if (save) insertCacheSearchUseCase(CacheSearch(id = 0, query = query))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                dispatch(Msg.Failed(e.message))
            }
        }
    }
}

private object ReducerImpl : Reducer<WeatherUIState, Msg> {           // pure: only applies facts
    override fun WeatherUIState.reduce(msg: Msg): WeatherUIState = when (msg) {
        is Msg.Loading -> copy(query = msg.query, loadState = LoadState.Loading)
        is Msg.Loaded -> copy(loadState = LoadState.Success, weatherUiModel = msg.weather, forecastUiModel = msg.forecast)
        is Msg.Failed -> copy(loadState = LoadState.Error(msg.message))
    }
}

// storeFactory.create(name = "Weather", initialState = WeatherUIState(),
//                     executorFactory = { ExecutorImpl(...) }, reducer = ReducerImpl)
```

## The two reducer families

MVIKotlin shows the other way to split the work:

| | MVIKotlin (executor decides) | Ours (reducer decides) |
|---|---|---|
| The reducer receives | `Message`: facts the executor has already decided ("Loaded") | `Event`: intents *and* results |
| The reducer returns | `State` | `Next(State, Effects, Commands)` |
| Validation, "save on success", races | Executor (impure) | Reducer (pure) |
| From Option A | Replace every `setState { }` with `dispatch(Msg.X)` | Move the decisions into the reducer |

It's a good learning step: Option A → MVIKotlin style → Elm style.

## What we take

1. **Main-thread confinement as a rule.** Like MVIKotlin, our `sendEvent` is documented as main-thread only. That's what makes it ordered without a mutex.
2. **Bootstrapper**: a named way to start work when the store is created. Ours is the `Started` event sent from `init`.
3. **Labels** are named one-shot outputs, the same idea as our `UIEffect`.

## References

- Store: https://arkivanov.github.io/MVIKotlin/store/
- Binding and lifecycle: https://arkivanov.github.io/MVIKotlin/binding_and_lifecycle/
- 4.4.0 release: https://github.com/arkivanov/MVIKotlin/releases/tag/4.4.0
