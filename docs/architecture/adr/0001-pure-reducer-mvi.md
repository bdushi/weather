# ADR 0001: Pure-reducer MVI (Elm-style commands)

- **Status:** Accepted
- **Date:** 2026-10-06
- **Scope:** every screen in `presentation/*`, base classes in `core/viewmodel`

## Context

The project has tried two MVI styles:

| Where | Style | Problem |
|---|---|---|
| `e168a4c` (`BaseViewModel`, event handler) | [Option A](../mvi-patterns/01-option-a-event-handler.md): `handleEvent` decides and runs work, calling `setState`/`setEffect` directly | State transitions are mixed into coroutines and can't be tested as pure functions |
| `develop` (`BaseViewModel` + `Reducer`) | [Hybrid](../mvi-patterns/02-develop-hybrid.md): the reducer returns `Pair<State, Effect?>`, then an overridden `sendEvent` has a second `when` that starts async work, and the results come back through `setState` | It pays for a reducer without getting the guarantee. Validation is duplicated, results bypass the reducer, and searches are never cancelled |

The current `BaseViewModel` also has these bugs:

- `onStart` runs again under `WhileSubscribed(5000)`, which adds another cache collector and refetches each time the app comes back from the background.
- `setState` isn't atomic.
- Effects are collected with `LaunchedEffect(Unit)`, which isn't lifecycle-aware.

Before deciding, we compared 4 in-house variants and 16 external libraries, frameworks and languages (see [mvi-patterns](../mvi-patterns/README.md)). Library facts were checked against their sources on 2026-10-06.

## Decision

Use a **pure reducer with commands**, following the Elm Architecture, implemented in our own small base in `core/viewmodel`. We don't add an MVI library dependency.

```
(State, Event) -> Next(State, Effects, Commands)
```

1. **The reducer is the only place state changes.** It's pure and synchronous: no `suspend`, no use cases, no `viewModelScope`.
2. **Async results are events** (`WeatherLoaded`, `WeatherFailed`, ...). They go back through `sendEvent` into the reducer.
3. **Side work is described as data.** The reducer returns `Command`s, and the ViewModel (the *executor*) runs them. The executor makes no business decisions.
4. **Effects are UI one-shots only** (snackbar, navigation). Commands are internal and the UI never sees them.
5. The base class exposes **no `setState`/`setEffect`**, so the compiler stops anyone from bypassing the reducer.

"Pure" applies to the reducer. The executor is impure by design. A **hybrid** is any code path that changes state without going through the reducer, and that is what we forbid.

The full reference implementation is in [04-elm-commands.md](../mvi-patterns/04-elm-commands.md). Ideas borrowed from other libraries are in [borrowed-ideas.md](../mvi-patterns/borrowed-ideas.md).

### Naming

- Base marker interfaces: `UIState`, `UIEvent`, `UIEffect` (renamed from `UiState`, `UiEvent`, `UiEffect`).
- The existing sealed `presentation/model/UIState` (`Loading`/`Success`/`Error`) becomes `LoadState`, and the field `uIState` becomes `loadState`. This frees the `UIState` name.
- Event names: `OnX` for UI intents, `XLoaded`/`XFailed`/`XChanged` for results.
- Command names describe the work to do: `Fetch`, `SaveQuery`, `DeleteQuery`.

### Borrowed infrastructure

From other libraries we take only infrastructure: `launchUnique`, `HandleEffects`, `MviHost`, transition logging, failure containment, a debug purity check, and exhaustive reducer tests. See [borrowed-ideas.md](../mvi-patterns/borrowed-ideas.md).

### Effect delivery: a queue held as state (decided 2026-10-06)

UI effects are **not** sent through a `Channel`. The reducer still returns `effects` in `Next`. The base appends them to a pending queue (`effects: StateFlow<List<Pending<Eff>>>`), and the UI removes each one with `effectHandled(id)` only *after* handling it, using `HandleEffects`, which runs only while STARTED.

Why: Google's "UI events" guidance says Channels don't guarantee delivery when the ViewModel outlives the UI. In practice, a snackbar still showing when the screen stops would be lost. With the queue it's shown again on return. Details and the alternatives we rejected: [borrowed-ideas.md, effect delivery](../mvi-patterns/borrowed-ideas.md#effect-delivery-a-queue-held-as-state).

## Consequences

**Positive**

- Each screen's full behavior lives in one pure function that you can read top to bottom.
- Reducer tests are plain JUnit, with no coroutines, mocks or Koin.
- Every transition goes through one `sendEvent`, which is where `core/logging` and `core/analytics` hook in.
- Races are handled the same way everywhere: a `requestId` in state guards correctness, and `launchUnique` cancels stale work.

**Negative (accepted)**

- More types per feature: intents, results and commands.
- More indirection: intent → reducer → command → executor → result → reducer.
- Bookkeeping fields in state (`requestId`, `pendingSave`) hold what the reducer needs to remember between asking for work and getting the answer.
- Contributors have to learn the loop.

**Mitigations**

- A screen whose reducer returns no commands costs about as much as Option A.
- Split a reducer by area once its `when` grows past about 15 branches.
- An Android Studio file template for State/Event/Effect/Command/Reducer.
- The transition log makes the indirection easier to debug: you read the event log instead of stepping through code.

## Alternatives considered

| Option | Why not chosen |
|---|---|
| Option A (event handler) | Simple, but decisions live in impure code. Fine for small apps, not what we want to learn and standardize |
| Keep the hybrid | It has the cost of a reducer without its guarantees |
| Reducer + state diff (`onTransition`) | Pure, but one-time actions ("save once on success") become unclear state-diff rules |
| Orbit, adidas MVI, MVIKotlin, Ballast, Mavericks | The executor or intent decides, so decisions stay in impure code. Used for inspiration only |
| Square Workflow, FlowRedux | State-machine model with a different mental model and more API surface. Used for inspiration |
| Mobius, TCA, Redux family, Elm/Elmish | The same model as ours. Mobius has a Java-first API with no Compose or KMP support; the others aren't Android. Used for inspiration |

## Revisit if

- Command and result-event boilerplate starts slowing the team down noticeably across many simple screens. Then consider Orbit as the fallback, accepting that decisions would move into impure code.
- We need state shared across screens. Reducers are per screen, so add a store or repository layer underneath.
