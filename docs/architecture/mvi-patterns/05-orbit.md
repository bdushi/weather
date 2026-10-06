# 05. Orbit MVI

| | |
|---|---|
| Family | Impure intent. `reduce {}` is a state-copy lambda called inside an impure `intent {}` |
| Who decides | `intent { }` (impure, suspending) |
| Platform | Kotlin Multiplatform (Android, iOS, desktop) |
| Version checked | `12.0.1` (2026-08-28) · ~1.3k ★ · Apache-2.0 |
| Status | **Inspiration.** It's the library that originally inspired this project, and the [fallback](../adr/0001-pure-reducer-mvi.md#revisit-if) if the pure reducer turns out too heavy |

## Core API (12.x, verified from source)

```kotlin
class WeatherViewModel(...) : ViewModel(),
    OrbitContainerHost<WeatherUIState, WeatherUIState, WeatherUIEffect> {   // <INTERNAL, EXTERNAL, SIDE_EFFECT>

    override val container = orbitContainer<WeatherUIState, WeatherUIEffect>(WeatherUIState())
    ...
}
```

- `OrbitContainerHost<INTERNAL_STATE, EXTERNAL_STATE, SIDE_EFFECT>`. Pass the same type twice if you don't need a separate UI-facing state. The old `ContainerHost<S, SE>` and `container()` are **deprecated** aliases.
- DSL: `intent { }`, `reduce { state.copy(...) }`, `postSideEffect(...)`, `subIntent { }`, `repeatOnSubscription { }`, `runOn { }`.
- `reduce` **can't call suspend functions**, and each `reduce` is **atomic** (`StateFlow.update`, compare-and-swap).
- **Intents run concurrently and aren't serialized.** Two intents can interleave, and their order isn't guaranteed. There's no built-in "latest wins": each `intent {}` returns a `Job`, and you cancel it yourself.
- Side effects go through `container.sideEffectFlow`. `SideEffectMode` (new in 12.0) is `FAN_OUT` (default), `FAN_OUT_STRICT` or `BROADCAST` (experimental).

> **Correction to our earlier discussion:** we called Orbit's `reduce` "serialized". It's atomic per call, but intents aren't serialized.

## Weather example

```kotlin
// Pure state transitions, testable with plain JUnit
fun WeatherUIState.startLoading(query: String) = copy(query = query, loadState = LoadState.Loading)
fun WeatherUIState.loaded(w: WeatherUiModel, f: ForecastUiModel) =
    copy(loadState = LoadState.Success, weatherUiModel = w, forecastUiModel = f)
fun WeatherUIState.failed(message: String?) = copy(loadState = LoadState.Error(message))

class WeatherViewModel(...) : ViewModel(), OrbitContainerHost<WeatherUIState, WeatherUIState, WeatherUIEffect> {
    override val container = orbitContainer<WeatherUIState, WeatherUIEffect>(WeatherUIState())

    private var searchJob: Job? = null

    fun search(query: String) {
        val error = validate(query)
        if (error != null) intent { postSideEffect(WeatherUIEffect.ShowError(error)) }
        else load(query, save = true)
    }

    fun retry() = load(container.stateFlow.value.query, save = false)

    private fun load(query: String, save: Boolean) {
        searchJob?.cancel()                                   // Orbit doesn't cancel for you
        searchJob = intent {
            reduce { state.startLoading(query) }
            try {
                val (w, f) = loadWeather(query)
                reduce { state.loaded(w, f) }
                if (save) insertCacheSearchUseCase(CacheSearch(id = 0, query = query))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                reduce { state.failed(e.message) }
            }
        }
    }

    fun deleteHistory(item: CacheSearchUiModel) = intent {
        try {
            deleteCacheSearchUseCase(CacheSearch(id = item.id, query = item.query))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            postSideEffect(WeatherUIEffect.ShowError(e.message ?: "Failed to delete"))
        }
    }
}
```

## Compose and testing

```kotlin
val state by viewModel.collectAsState()                              // lifecycle-aware (orbit-compose)
viewModel.collectSideEffect(lifecycleState = Lifecycle.State.STARTED) { effect -> ... }
```

```kotlin
// orbit-test, 12.x: test() is deprecated in favor of testWithInternalState / testWithExternalState
viewModel.testWithInternalState(this, WeatherUIState()) {
    containerHost.search("Paris")
    expectInternalState { startLoading("Paris") }
    expectInternalState { loaded(paris, parisForecast) }
}
```

## Compared with ours

| Orbit | Ours ([04](04-elm-commands.md)) |
|---|---|
| Decisions in `intent {}`, which is impure and suspending | Decisions in the pure reducer |
| Intents run concurrently | Events processed one at a time, in order |
| Cancel the returned `Job` yourself | `launchUnique` plus `requestId` |
| "Save on success" is a line after `reduce` | A `pendingSave` field, then a `SaveQuery` command |

## What we take

1. **Handle effects only while STARTED** (`collectSideEffect(lifecycleState = STARTED)`), which our `HandleEffects` composable does as well. Unlike Orbit's flow, our effects stay queued until handled ([borrowed-ideas, effect delivery](borrowed-ideas.md#effect-delivery-a-queue-held-as-state)).
2. **A host interface** (`OrbitContainerHost`), which became our `MviHost`, so the UI depends on an interface.
3. **Atomic state writes.** Ours is stronger: a single-threaded `sendEvent` means the reducer can't be re-run by a compare-and-swap retry.
4. **`repeatOnSubscription`**: run a stream only while the UI is subscribed. Noted for later; we start `ObserveHistory` once.
5. **A ViewModel test DSL** in the style of `expectState` / `expectSideEffect` for executor tests.

## References

- Core: https://orbit-mvi.org/Core/
- Compose: https://orbit-mvi.org/Compose/
- Test: https://orbit-mvi.org/Test/ (the repo docs still show the deprecated `.test(this)`)
- 12.0.0 release notes: https://github.com/orbit-mvi/orbit-mvi/releases/tag/12.0.0
