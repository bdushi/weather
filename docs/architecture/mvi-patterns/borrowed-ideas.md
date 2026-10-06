# Borrowed ideas

What we take from each library into our own base ([04](04-elm-commands.md)). These ideas add infrastructure only. The decision model stays the pure reducer.

| # | Idea | From | Where it lands |
|---|---|---|---|
| 1 | Cancel the previous job of the same kind | adidas `UniqueIntent`, TCA `cancelInFlight`, NgRx `switchMap`, FlowRedux `CancelPrevious`, Ballast `sideJob(key)` | `MviViewModel.launchUnique(key)` |
| 2 | Effects held as state until handled, processed only while STARTED | Google "UI events" guidance, adidas `SideEffects`, TCA `@Presents`, Orbit `collectSideEffect(STARTED)` | Pending queue in `MviViewModel` + `HandleEffects` composable |
| 3 | A host interface the UI depends on | Orbit `OrbitContainerHost`, adidas `MviHost` | `MviHost<S, Ev, Eff>` |
| 4 | One place that logs every transition | adidas logger, Ballast debugger | `onTransition(event, old, next)` → `core/logging`, `core/analytics` |
| 5 | A failing reducer doesn't corrupt state | adidas | `onReducerError`: keep the old state; rethrow in debug |
| 6 | Purity check in debug | Mavericks (runs reducers twice) | `sendEvent` reduces twice in debug and compares |
| 7 | Exhaustive reducer tests | TCA `TestStore`, Mobius `UpdateSpec` | Assert the whole `Next`, never selected fields |
| 8 | Derived values as functions, not fields | Reselect / NgRx selectors | Extension properties on the state |
| 9 | Sealed states where states are exclusive | FlowRedux `inState<>`, Workflow | Per screen, when it fits |
| 10 | Commands as comparable data | redux-loop, Mobius | Already in [04](04-elm-commands.md) (an improvement over Elm/TCA closures) |

## 1. `launchUnique`

```kotlin
private val uniqueJobs = mutableMapOf<Any, Job>()

/** Starting work under a key cancels any running work with the same key. Main thread only. */
protected fun launchUnique(key: Any, block: suspend CoroutineScope.() -> Unit) {
    uniqueJobs[key]?.cancel()
    uniqueJobs[key] = viewModelScope.launch(block = block)
}
```

This is NgRx's `switchMap` / FlowRedux's `CancelPrevious`. It saves work. **Correctness still comes from `requestId` in the reducer**, because that's what tests can see.

## 2. Effect delivery

See [Effect delivery: a queue held as state](#effect-delivery-a-queue-held-as-state) below. The code is in [04](04-elm-commands.md#screen).

## 4–6. Inside `sendEvent`

```kotlin
final override fun sendEvent(event: Ev) {
    val old = _state.value
    val next = try {
        reducer.reduce(old, event).also { first ->
            if (debugChecks) check(reducer.reduce(old, event) == first) { "Reducer is not pure for $event" }   // 6
        }
    } catch (e: Exception) {
        onReducerError(event, old, e)                                                                     // 5
        return
    }
    _state.value = next.state
    onTransition(event, old, next)                                                                        // 4
    if (next.effects.isNotEmpty()) _effects.update { q -> q + next.effects.map { Pending(nextEffectId++, it) } }
    next.commands.forEach { execute(it) }
}
```

`debugChecks` is passed in by the app module (e.g. `BuildConfig.DEBUG`), because `core/viewmodel` doesn't see the app's `BuildConfig`. Calling `reduce` twice is cheap, because reducers do no I/O.

## 8. Derived values

```kotlin
// Don't store it in state (the reducer would have to keep it in sync):
val WeatherUIState.showEmptyHistory: Boolean get() = cacheSearch.isEmpty() && loadState is LoadState.Success
```

## Effect delivery: a queue held as state

**Decided 2026-10-06.** UI effects are held as a pending queue in state, and the UI removes each one after handling it. We don't use a `Channel`.

Why, in Google's words (Android Developers, "UI events", updated 2026-05-18):

> "UI actions that originate from the ViewModel—ViewModel events—should always result in a UI state update."
>
> "When the producer (the ViewModel) outlives the consumer (Compose UI), these solutions [Channels] don't guarantee the delivery and processing of those events."

Manuel Vivo, "ViewModel: One-off event antipatterns" (2022), adds that the `Main.immediate` workaround is "error-prone as devs could easily forget it".

**The concrete failure this prevents.** `snackbar.showSnackbar(...)` suspends. With a Channel, if the screen stops while the snackbar is showing, the collector is cancelled mid-handling, and the effect is lost because it was already taken out of the Channel. With the queue, the effect stays until `effectHandled(id)` is called, so it's shown again when the screen returns.

**How it works**

```kotlin
data class Pending<out Eff : UIEffect>(val id: Long, val effect: Eff)

// MviViewModel
private val _effects = MutableStateFlow<List<Pending<Eff>>>(emptyList())
final override val effects: StateFlow<List<Pending<Eff>>> = _effects.asStateFlow()
final override fun effectHandled(id: Long) { _effects.update { q -> q.filterNot { it.id == id } } }
```

- **Reducers are unchanged.** They still return `Next(state, effects = listOf(...))`, and reducer tests stay the same.
- **The queue is delivery bookkeeping, not screen state.** It lives in the base next to the screen state, not inside it. The reducer never reads it, so it's not a hybrid.
- **The UI handles one effect at a time** with `HandleEffects(host) { ... }`. That runs only while STARTED and confirms each effect *after* the handler finishes. See [04](04-elm-commands.md#screen).
- **Ids, not equality.** Two identical `ShowError("x")` effects are two separate entries.

**Limits we accept**

- The queue doesn't survive process death, the same as a Channel. Effects are short-lived, so that's acceptable.
- Navigation effects have to be confirmed *after* navigating. Otherwise returning to the screen could navigate again.

**Alternatives considered**

| Option | Why not |
|---|---|
| `Channel` + a `CollectEffects` helper on `Main.immediate` | Loses an effect whose handler suspends (snackbar) when the screen stops mid-handling |
| Effects as fields inside each screen's state, cleared by an `XShown` event through the reducer | Pure, but every screen needs its own fields and a "shown" event for each effect. The generic queue gives the same guarantee once, in the base |
| adidas-style `State<TView, TSideEffect>` wrapper | Wraps every screen state type. Our queue sits next to the state instead |

## Rejected

| Idea | From | Why not |
|---|---|---|
| Decisions in `intent {}` / executor | Orbit, adidas, MVIKotlin, Ballast, Mavericks | Takes decisions out of the pure reducer, which is the thing we chose |
| Named `StateTransform` classes | adidas | Our result events are already named. Transforms would name each change twice |
| Effects stored *inside the screen state* type (`State<TView, TSideEffect>`) | adidas | We kept the idea (effects as state) but put the queue in the base, next to the state, so screen state types stay clean |
| `UniqueIntent` *instead of* `requestId` | adidas | Cancellation isn't visible to reducer tests. Keep both |
| A dependency on an MVI library | all | Our base is about 80 lines. A library would add a second set of concepts, and none matches "pure reducer + data commands + Compose" exactly |
