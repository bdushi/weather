# MVI in this project

How we build screens. **Why** we do it this way: [ADR 0001](docs/architecture/adr/0001-pure-reducer-mvi.md). **What else we considered** (13 pages, the same weather example in each): [MVI pattern catalog](docs/architecture/mvi-patterns/README.md).

## The loop

```
(State, Event) -> Next(State, Effects, Commands)
```

```
UI ──Event──▶ REDUCER (pure) ──State──▶ UI
                 │       └──Effects──▶ UI (snackbar, navigation)
                 └─Commands─▶ EXECUTOR (ViewModel: use cases, DB)
                                   └──result Event──▶ REDUCER
```

| Type | Direction | Example | Who sees it |
|---|---|---|---|
| `UIState` | Reducer → UI | `loadState = Loading` | UI (`StateFlow`) |
| `UIEvent` | UI or executor → reducer | `Search("Paris")`, `WeatherLoaded(...)` | Reducer |
| `UIEffect` | Reducer → UI, one-shot | `ShowError("too short")` | UI (queued until handled) |
| `Command` | Reducer → executor | `FetchWeather(id, City("Paris"))` | Executor only |

## Rules

1. **The reducer is the only place state changes.** It's pure and synchronous: no `suspend`, no use cases, no `viewModelScope`, no clock or randomness.
   > "Reducers Must Not Have Side Effects." (Redux Style Guide)
2. **The reducer decides; the executor does.** The executor turns commands into use-case calls and results into events. It never makes a business decision and never reads `state.value` to make one. Anything it needs goes in the command.
3. **Async results are events** (`XLoaded`, `XFailed`, `XChanged`). They go back through `sendEvent`. This includes callbacks such as a location fix (`LocationResolved`). The executor never chains one piece of work into the next by itself; the reducer decides the next command.
4. **There is no `setState` and no `setEffect`.** A code path that changes state without going through the reducer is a *hybrid*, and that's not allowed.
5. **What the reducer must remember between asking and answering goes into state** (`requestId` for stale results, `pendingSave` for "do X if it succeeds").
6. **Effects are UI-only. Commands are internal.** Never collect effects to trigger logic. Effects wait in a queue until the UI handles them with `HandleEffects`, which calls `effectHandled(id)` *after* the work is done (after the snackbar finishes, or after navigating). We don't use a `Channel`.
7. **Start a screen from `init { sendEvent(Started) }`**, not from `onStart` with `WhileSubscribed`.
8. **`sendEvent` runs on the main thread.** Events are processed one at a time, in order.
9. **Every reducer has a plain JUnit test that asserts the whole `Next`.**
10. **Derived values are extension properties on the state**, not stored fields.

## Naming

| Kind | Pattern | Examples |
|---|---|---|
| UI intent | `OnX` / verb | `OnRetry`, `OnQueryChange`, `Search` |
| Result | `XLoaded` / `XFailed` / `XChanged` / `XResolved` | `WeatherLoaded`, `DeleteFailed`, `HistoryChanged`, `LocationResolved` |
| Command | `FetchX` / `ResolveX` / `SaveX` / `DeleteX` / `ObserveX` | `FetchWeather`, `ResolveLocation`, `SaveQuery`, `ObserveHistory` |
| Effect | `ShowX` / `NavigateX` | `ShowError` |
| Load status | `LoadState.Loading / Success / Error` | (renamed from the old sealed `UIState`) |

## Files per screen

```
presentation/<feature>/
  <Feature>UIState.kt      data class : UIState         (+ bookkeeping fields)
  <Feature>UIEvent.kt      sealed interface : UIEvent   (intents, then results)
  <Feature>UIEffect.kt     sealed interface : UIEffect
  <Feature>Command.kt      sealed interface              (+ value types such as WeatherTarget)
  <Feature>Reducer.kt      object : Reducer<…>          ← the screen's behavior
  <Feature>ViewModel.kt    MviViewModel<…>, implements execute()
  <Feature>Screen.kt       Route (MviHost) + stateless Screen
test/
  <Feature>ReducerTest.kt
```

A simple screen whose reducer returns no commands needs no `Command` file and an empty `execute`.

## Base API (`core/viewmodel`)

| Member | Purpose |
|---|---|
| `sendEvent(event)` | The only entry point. Reduce, log, emit effects, run commands |
| `execute(command)` | Abstract: run the work, report back with `sendEvent` |
| `launchUnique(key) { }` | Starting new work under a key cancels the old work (switch-latest) |
| `onTransition(event, old, next)` | Hook for logging and analytics |
| `onReducerError(event, state, e)` | Rethrows by default; release builds may log and keep the old state |
| `effects` / `effectHandled(id)` | Pending UI effects as `StateFlow<List<Pending<Eff>>>`, and removing one once it's handled |
| `HandleEffects(host) { }` | Composable in `presentation/ui`: handles queued effects one at a time while STARTED, then confirms each one |
| `debugChecks` (constructor) | Pass `BuildConfig.DEBUG` (enable `buildFeatures.buildConfig` in the feature module): reduces every event twice and fails if the results differ |
| `MviHost<S, Ev, Eff>` | What the UI depends on, so previews and tests can pass a fake |

Full code, copied from the implementation: [04-elm-commands.md](docs/architecture/mvi-patterns/04-elm-commands.md). The source of truth is `core/viewmodel/MviViewModel.kt` and `MviContract.kt`.

## Checklist

```
Is it something the user did?               → Intent event (OnX)
Is it the result of async work?             → Result event (XLoaded / XFailed)
Does it need an API / DB / location call?   → Reducer returns a Command; executor runs it
Is it data the UI renders?                  → State
Is it a one-shot UI reaction (snackbar)?    → Effect
Is it a rule ("valid?", "save if…")?        → Reducer
Can it be computed from state?              → Extension property, not a field
```

## Pitfalls

- ❌ `setState` from a coroutine, which is the hybrid.
- ❌ A second `when (event)` outside the reducer.
- ❌ `catch (e: Exception)` without rethrowing `CancellationException`.
- ❌ An executor that checks business rules.
- ❌ The UI sending result events.
- ❌ Asserting only some fields of `Next` in reducer tests.
- ❌ Calling `effectHandled` before the effect's work has finished, or collecting effects with a bare `LaunchedEffect(Unit)`.
