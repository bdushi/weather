# MVI patterns: reference catalog

Every approach we looked at before choosing ours, written against the **same weather scenario** so you can compare them directly. The decision itself is in [ADR 0001](../adr/0001-pure-reducer-mvi.md). The rules for everyday work are in [`MVI.md`](../../../MVI.md).

> Library facts (versions, API names, dates) were checked against each project's source, releases and docs on **2026-10-06**. Code sketches haven't been compiled. They show the shape of each approach, not copy-paste code.

## The scenario

- **Search**: validate the query (at least 2 characters, otherwise show an error snackbar), show Loading, fetch weather and forecast in parallel, show the result or an error, and **save the query to history only on success**. A new search **cancels or ignores** the previous one.
- **Retry**: reload the current query without saving.
- **DeleteHistory**: delete the item, and show an error snackbar if that fails.

Shared code used by every page:

```kotlin
sealed interface LoadState {
    data object Loading : LoadState
    data object Success : LoadState
    data class Error(val message: String?) : LoadState
}

/** Pure validation: an error message, or null if valid. */
fun validate(query: String): String? = when {
    query.isBlank() -> "Search query cannot be empty"
    query.trim().length < 2 -> "Search query must be at least 2 characters"
    else -> null
}

/** Weather + forecast in parallel. Throws on failure. */
suspend fun loadWeather(query: String): Pair<WeatherUiModel, ForecastUiModel>
```

## Catalog

| # | Approach | Family | Who decides | Platform | Status |
|---|---|---|---|---|---|
| [01](01-option-a-event-handler.md) | Option A: event handler (`e168a4c`) | Impure handler | ViewModel | — | Former, reference |
| [02](02-develop-hybrid.md) | `develop` hybrid | Mixed ⚠ | Reducer **and** `sendEvent` | — | **Anti-pattern**, being replaced |
| [03](03-reducer-state-diff.md) | Reducer + state diff (`onTransition`) | Pure reducer | Reducer | — | Not chosen |
| [04](04-elm-commands.md) | **Pure reducer with commands** | Pure reducer | Reducer | — | ⭐ **Chosen** |
| [05](05-orbit.md) | Orbit MVI 12.0.1 | Impure intent | `intent {}` | KMP | Inspiration · fallback |
| [06](06-adidas-mvi.md) | adidas MVI 1.9.5 | Executor + transforms | Executor | KMP | Inspiration |
| [07](07-mvikotlin.md) | MVIKotlin 4.4.0 | Executor + reducer | Executor | KMP | Inspiration |
| [08](08-mobius.md) | Mobius 2.1.2 | Pure reducer | `Update` | JVM | Inspiration |
| [09](09-tca.md) | The Composable Architecture 1.26.2 | Pure reducer | Reducer | Swift | Inspiration (study first) |
| [10](10-redux-family.md) | Redux 5 · RTK 2.13 · redux-loop 6.2 · NgRx 22 · ReduxKotlin 1.0-alpha | Pure reducer | Reducer (+ middleware / effects) | JS, Angular, KMP | Inspiration |
| [11](11-elm-elmish.md) | Elm 0.19.3 · Elmish 5.0.2 | Pure reducer (origin) | `update` | Web, F# | Study |
| [12](12-state-machines.md) | Square Workflow 1.31 · FlowRedux 2.1.1 | State machine | Actions / per-state handlers | KMP | Inspiration |
| [13](13-ballast-mavericks.md) | Ballast 5.1 · Mavericks 3.1.1 | Impure handler | Handler / ViewModel | KMP / Android | Inspiration |
| — | [Borrowed ideas](borrowed-ideas.md) | | | | What we take, and the effect-delivery decision (a queue held as state) |

## Side by side

| Approach | Validation in | "Save on success" in | Stale results handled by | Pure-test coverage |
|---|---|---|---|---|
| 01 Option A | ViewModel | Coroutine (`if (save)`) | `searchJob.cancel()` | Low |
| 02 Hybrid | Reducer **and** `sendEvent` ⚠ | Coroutine | Nothing ⚠ | Partial |
| 03 State diff | Reducer | `pendingSave` + `onTransition` rule | `requestId` | High |
| **04 Commands** | **Reducer** | **Reducer → `SaveQuery`** | **`requestId` (+ `launchUnique`)** | **Highest** |
| 05 Orbit | ViewModel | Intent | `Job.cancel()` | Medium (pure state functions) |
| 06 adidas | Executor flow | Executor flow | `UniqueIntent` | Medium (transforms) |
| 07 MVIKotlin | Executor | Executor | `Job.cancel()` | Medium (reducer) |
| 08 Mobius | `update` | `update` → effect | `requestId` | Highest |
| 09 TCA | Reducer | Reducer → `.run` | `.cancellable(id:cancelInFlight:)` | Highest (exhaustive `TestStore`) |
| 12 FlowRedux | Handler | `onEnter` handler | Leaving the state cancels | Medium |

The pattern to notice: **01, 05, 06, 07 and 13 make decisions in impure code**, where async logic is easy to write. **03, 04, 08, 09, 10 and 11 make decisions in pure code**, which is easy to test but needs `requestId`/`pendingSave` bookkeeping. **02 is the one to avoid.**

## Family tree

```
André Staltz, "Reactive MVC and the Virtual DOM" (2014) ── names Model-View-Intent → Cycle.js
Elm (2012, web language) ── pure update + Cmd loop
 ├─▶ Elmish (F#)
 ├─▶ Redux (JS, 2015) ── pure reducers; side effects in middleware
 │    ├─▶ redux-loop ── Elm's Cmd back in Redux
 │    ├─▶ NgRx ── effects: actions in → actions out
 │    ├─▶ ReduxKotlin
 │    └─▶ React useReducer + useEffect ─────────────── (03)
 ├─▶ Mobius (Spotify) ── Elm's loop on the JVM ────────── (08)
 ├─▶ TCA (Point-Free, Swift) ─────────────────────────── (09)
 └─▶ Hannes Dorfmann, "Reactive Apps with MVI" (2017) → Android MVI articles
       └─▶ "reducer + async elsewhere" ───────────────── (02)
                                     our choice ──────── (04) = Elm/Mobius/TCA shape in Kotlin

Executor decides, reducer applies:  MVIKotlin · adidas MVI
Impure handler + atomic setState:   Orbit · Mavericks · Ballast · Option A (01)
State machines:                     Square Workflow · FlowRedux
```

## Further reading

**Origins**
- André Staltz (byline Andre Medeiros), "Reactive MVC and the Virtual DOM", 2014-11-02: https://www.futurice.com/blog/reactive-mvc-and-the-virtual-dom
- André Staltz, "Unidirectional User Interface Architectures", 2015-08-22: https://staltz.com/unidirectional-user-interface-architectures.html
- Cycle.js, Model-View-Intent: https://cycle.js.org/model-view-intent.html
- Hannes Dorfmann, "Reactive Apps with Model-View-Intent", parts 1–8 (2017–2018). Part 1: https://hannesdorfmann.com/android/mosby3-mvi-1/ · Part 3, State Reducer: https://hannesdorfmann.com/android/mosby3-mvi-3/ · Part 7, Timing / SingleLiveEvent: https://hannesdorfmann.com/android/mosby3-mvi-7/
- The Elm guide: https://guide.elm-lang.org/architecture/ and https://guide.elm-lang.org/effects/

**Android guidance**
- UI events: https://developer.android.com/topic/architecture/ui-layer/events
- Manuel Vivo, "ViewModel: One-off event antipatterns" (2022-06-01): https://medium.com/androiddevelopers/viewmodel-one-off-event-antipatterns-16a1da869b95 (mirror: https://manuelvivo.dev/viewmodel-events-antipatterns)
- Lifecycle in Compose (`collectAsStateWithLifecycle`, `LifecycleStartEffect`): https://developer.android.com/topic/libraries/architecture/compose. Lifecycle releases (stable 2.11.0): https://developer.android.com/jetpack/androidx/releases/lifecycle

**Where our first `BaseViewModel` came from**
- Yusuf Ceylan, "MVI Architecture with Kotlin Flows and Channels" (ProAndroidDev, 2021-01-11): https://proandroiddev.com/mvi-architecture-with-kotlin-flows-and-channels-d36820b2028d · sample: https://github.com/yusufceylan/MVI-Playground. Its `setState` / `setEffect` / `Channel<Effect>` base is the ancestor of [01](01-option-a-event-handler.md).
