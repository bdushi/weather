# 12. State-machine libraries: Square Workflow and Freeletics FlowRedux

Both treat a screen as a **state machine**: what can happen depends on which state you're in, and work is tied to the state that started it.

| | Square Workflow | Freeletics FlowRedux |
|---|---|---|
| Version checked | `1.31.0` (2026-08-25) · ~1.1k ★ · Apache-2.0 | `2.1.1` (2026-06-24) · ~780 ★ · Apache-2.0 |
| Platform | KMP (JVM/Android, iOS, JS). UI: Android Views, Compose, Compose Multiplatform | KMP (Android, JVM, native, JS, Wasm) |
| Who decides | `render()` (declarative, no side effects) + `WorkflowAction`s that set state | Impure suspend handlers that **return** a pure `ChangedState` |
| Status | **Inspiration**: composition, and work tied to state | **Inspiration**: per-state handlers and execution policies |

## Square Workflow

```kotlin
object WeatherWorkflow : StatefulWorkflow<Unit, WeatherState, Nothing, WeatherRendering>() {
    override fun initialState(props: Unit, snapshot: Snapshot?) = WeatherState.Idle(DEFAULT_QUERY)

    override fun render(renderProps: Unit, renderState: WeatherState, context: RenderContext): WeatherRendering {
        if (renderState is WeatherState.Loading) {
            // Declared, not launched: runs while render keeps declaring it, cancelled when it stops
            context.runningWorker(weatherWorker(renderState.query), key = renderState.query) { result ->
                action("weatherLoaded") { state = WeatherState.Loaded(result) }
            }
        }
        return WeatherRendering(
            state = renderState,
            onSearch = context.eventHandler("onSearch") { query: String -> state = WeatherState.Loading(query) },
        )
    }

    override fun snapshotState(state: WeatherState): Snapshot? = null
}
```

- `StatefulWorkflow<PropsT, StateT, OutputT, RenderingT>` with `initialState`, `render` and `snapshotState`.
- **State changes only in `WorkflowAction`** (`action("name") { state = ...; setOutput(...) }`). The unnamed `action {}` was removed in 1.13.
- **Side effects are declared during `render`** (`runningWorker`, `runningSideEffect(key)`). They keep running while each render pass declares them and are cancelled when a pass stops declaring them. This is the same idea as Elm's subscriptions.
- **Composition**: `context.renderChild(child, props, key) { output -> action("...") { ... } }`. Parents pass props down and get renderings and outputs back. Children that stop being rendered are torn down.
- Testing: `testRender(props).expectWorker(...).render { }.verifyActionResult { }` for single render passes, and `renderForTest` + `WorkflowTurbine` for integration tests. `testFromStart` and `launchForTestingFromStartWith` are gone or deprecated.

## Freeletics FlowRedux (v2 API)

```kotlin
class WeatherStateMachine(...) : FlowReduxStateMachineFactory<WeatherState, WeatherAction>() {
    init {
        initializeWith { WeatherState.Idle(DEFAULT_QUERY) }
        spec {
            inState<WeatherState.Idle> {
                on<WeatherAction.Search> { action -> override { WeatherState.Loading(action.query) } }
            }
            inState<WeatherState.Loading> {
                onEnter {                                        // cancelled automatically if we leave Loading
                    val (w, f) = loadWeather(snapshot.query)     // impure handler...
                    override { WeatherState.Loaded(w, f) }       // ...returns a pure state change
                }
                on<WeatherAction.Search> { action -> override { WeatherState.Loading(action.query) } }
            }
        }
    }
}
// Compose: val sm = factory.produceStateMachine(); sm.state.value; sm.dispatchAction(...)
```

- **Major rename in 2.0** (2025-11): the coordinates changed to `com.freeletics.flowredux2`, the spec moved to `FlowReduxStateMachineFactory`, it's started with `launchIn`/`shareIn`/`produceStateMachine`, and `ChangeableState` is now the receiver (`override {}`, `mutate {}`, `noChange()`). **The repo README still shows the v1 API.**
- `inState<S> { on<A>{} ; onEnter{} ; collectWhileInState(flow){} ; untilIdentityChanged({ ... }){} }`. Everything inside is **cancelled when the machine leaves `S`**.
- `ExecutionPolicy`: `CancelPrevious` (the default for `on`), `Ordered`, `Unordered`, `Throttled`.
- Testing: Turbine on `factory.shareIn(backgroundScope)`. Start from any state with `initializeWith { testState }`.

## Compared with ours

| Idea | Workflow / FlowRedux | Ours |
|---|---|---|
| Work tied to state | Declared per state and cancelled when the state is left | Commands are one-shot. Cancellation is by key (`launchUnique`) and `requestId` |
| Valid actions depend on the state | `inState<Loading> { on<Search> }` | One `when (event)`. Checking state inside is up to you |
| Composition | `renderChild` / sub-state machines | Split reducers by hand |

## What we take

1. **Sealed states where they help.** If a screen's states really are exclusive (Idle / Loading / Loaded / Error), make the state a sealed type and `when (state to event)` in the reducer. Impossible combinations then can't be represented.
2. **Long-running work tied to state** (Elm subscriptions, Workflow workers, `collectWhileInState`). If we need it later, a command like `ObserveX` paired with `StopObservingX`, both issued by the reducer, gives the same effect.
3. **Execution-policy names.** `CancelPrevious` = our `launchUnique`. Add `Ordered` / `Throttled` variants only when a screen needs them.

## References

- Workflow: https://github.com/square/workflow-kotlin · concepts: https://square.github.io/workflow/userguide/concepts/ · releases: https://github.com/square/workflow-kotlin/releases
- FlowRedux changelog (2.0.0): https://github.com/freeletics/FlowRedux/blob/main/CHANGELOG.md · DSL cheat sheet: https://freeletics.github.io/FlowRedux/dsl-cheatsheet/ · testing: https://freeletics.github.io/FlowRedux/user-guide/14_testing/
