# 13. Ballast and Mavericks

Both are impure-handler libraries in the same family as [Option A](01-option-a-event-handler.md) and [Orbit](05-orbit.md). Each has one idea worth copying.

| | Ballast | Airbnb Mavericks |
|---|---|---|
| Version checked | `5.1.0` (2026-06-10) · ~160 ★ · BSD-3-Clause | `3.1.1` (2026-09-25) · ~5.9k ★ · Apache-2.0 |
| Platform | KMP (JVM, Android, iOS, JS, Wasm-JS) | Android (`mvrx-common` has an experimental JVM `MavericksRepository`) |
| Who decides | `InputHandler.handleInput` (impure, suspend) | ViewModel methods (impure). `setState` lambdas must be pure |
| Status | **Inspiration: tooling** | **Inspiration: purity checks, the `Async` type** |

## Ballast

```kotlin
class WeatherInputHandler(...) : InputHandler<WeatherInput, WeatherEvent, WeatherState> {
    override suspend fun InputHandlerScope<WeatherInput, WeatherEvent, WeatherState>.handleInput(input: WeatherInput) =
        when (input) {
            is WeatherInput.Search -> {
                updateState { it.copy(query = input.query, loading = true) }
                val (w, f) = loadWeather(input.query)
                updateState { it.copy(loading = false, weather = w, forecast = f) }
            }
            is WeatherInput.ObserveHistory -> sideJob("history") {          // same key restarts the job
                getCacheSearchUseCase().collect { postInput(WeatherInput.HistoryChanged(it)) }
            }
            ...
        }
}
```

- `InputHandlerScope`: `getCurrentState()`, `updateState {}`, `postEvent()`, `sideJob(key) {}`, `cancelSideJob(key)`, `noOp()`.
- Input strategies: `FifoInputStrategy` (queue), `LifoInputStrategy` (a new input cancels the running one; the historical default) and `ParallelInputStrategy`. **The docs recommend always setting `FifoInputStrategy.typed()` explicitly.**
- **Tooling**: the **Ballast Debugger** IntelliJ plugin connects over WebSocket (localhost:9684) through `BallastDebuggerInterceptor`, and shows every input, event and state live. There are also undo/time-travel, saved-state, logging and analytics modules.
- Testing: `ballast-test`, using `viewModelTest { scenario("…") { given { state }; running { postInput(...) }; resultsIn { ... } } }`.

## Mavericks

```kotlin
class WeatherViewModel(initial: WeatherState) : MavericksViewModel<WeatherState>(initial) {
    fun search(query: String) {
        setState { copy(query = query) }
        suspend { loadWeather(query) }.execute { copy(weather = it) }   // it: Async<Pair<…>>
    }
}
data class WeatherState(val query: String = "", val weather: Async<Pair<WeatherUiModel, ForecastUiModel>> = Uninitialized) : MavericksState
```

- `setState { copy(...) }` lambdas are queued and run **one at a time on a background store thread**. **In debug builds, each reducer runs twice to catch impurity.**
- `Async<T>`: `Uninitialized`, `Loading`, `Success`, `Fail`, filled in by `execute {}`.
- No effects channel: one-shots are modeled in state and watched with `onEach` / `onAsync`.
- Testing: `MavericksTestRule` (JUnit 4) / `MavericksTestExtension` (JUnit 5) make the store synchronous.

## What we take

1. **(Mavericks) Check purity in debug builds.** `MviViewModel.sendEvent` can call `reducer.reduce` **twice** in debug and `check(first == second)`. That catches a reducer that reads the clock, randomness or a mutable field. This costs about 3 lines, and it's the cheapest automatic enforcement of rule 1 we've found.
2. **(Ballast) Transition tooling.** We won't add the plugin, but the `onTransition` hook carries the same information (event, old state, new state, effects, commands). A debug-only in-memory ring buffer of the last N transitions, attached to crash reports, gives most of the value.
3. **(Ballast) A single input queue as the default.** Our main-thread synchronous `sendEvent` is FIFO by construction.
4. **(Mavericks) `Async<T>`.** We have `LoadState`, which is enough for one load per screen. If a screen gets several independent loads, use a generic `Async<T>`-style type per field instead of one global `loadState`.

## References

- Ballast: https://github.com/copper-leaf/ballast · features: https://github.com/copper-leaf/ballast/blob/main/docs/feature-overview.md · debugger plugin: https://plugins.jetbrains.com/plugin/18702-ballast
- Mavericks: https://airbnb.io/mavericks/#/core-concepts · threading: https://airbnb.io/mavericks/#/threading · testing: https://airbnb.io/mavericks/#/testing
