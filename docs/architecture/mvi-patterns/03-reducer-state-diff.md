# 03. Reducer + state diff (`onTransition`)

| | |
|---|---|
| Family | Pure reducer, with side effects that react to state changes (React's `useReducer` + `useEffect`) |
| Who decides | The pure reducer, which writes its decision into state |
| In this repo | Never implemented. Designed as the smallest fix to [02](02-develop-hybrid.md) |
| Status | **Not chosen.** The upgrade path to [04](04-elm-commands.md) is mechanical |

## Idea

Keep `Pair<State, Effect?>`, but:

1. Results become events, and `setState` is removed.
2. The reducer records its **decision in state**, e.g. `request = FetchRequest(id = 2, query = "Paris")`.
3. The ViewModel **reacts to what changed** (`old.request != new.request → fetch`) instead of deciding again.

This is how React does it: `useReducer` stays pure, and `useEffect` runs side effects when state changes.

## Base class

```kotlin
abstract class BaseViewModel<State : UIState, Event : UIEvent, Effect : UIEffect>(initialState: State) : ViewModel() {
    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<State> = _state.asStateFlow()
    private val _effects = Channel<Effect>(Channel.BUFFERED)
    val effects: Flow<Effect> = _effects.receiveAsFlow()

    protected abstract val reducer: Reducer<State, Event, Effect>     // Pair<State, Effect?>

    fun sendEvent(event: Event) {                                      // final, main thread
        val old = _state.value
        val (new, effect) = reducer.reduce(old, event)
        _state.value = new
        effect?.let { _effects.trySend(it) }
        onTransition(event, old, new)
    }

    /** Run async work based on what the reducer decided. Report back only via sendEvent(). */
    protected open fun onTransition(event: Event, old: State, new: State) {}
}
```

## Weather example

```kotlin
data class FetchRequest(val id: Long, val query: String)

data class WeatherUIState(
    /* ...UI fields... */
    val request: FetchRequest? = null,      // reducer's decision: "fetch this"
    val pendingSave: String? = null,        // reducer's decision: "save if it succeeds"
) : UIState

override val reducer = object : Reducer<WeatherUIState, WeatherUIEvent, WeatherUIEffect> {
    override fun reduce(state: WeatherUIState, event: WeatherUIEvent) = when (event) {
        is WeatherUIEvent.Search ->
            validate(event.query)?.let { state to WeatherUIEffect.ShowError(it) }
                ?: (state.startFetch(event.query, save = true) to null)
        WeatherUIEvent.OnRetry -> state.startFetch(state.query, save = false) to null
        is WeatherUIEvent.OnDeleteCacheSearch -> state to null
        is WeatherUIEvent.WeatherLoaded ->
            if (event.requestId != state.request?.id) state to null            // stale → ignore
            else state.copy(
                loadState = LoadState.Success,
                weatherUiModel = event.weather,
                forecastUiModel = event.forecast,
                pendingSave = null,
            ) to null
        is WeatherUIEvent.WeatherFailed ->
            if (event.requestId != state.request?.id) state to null
            else state.copy(loadState = LoadState.Error(event.message), pendingSave = null) to null
        is WeatherUIEvent.DeleteFailed ->
            state to WeatherUIEffect.ShowError(event.message ?: "Failed to delete")
    }

    private fun WeatherUIState.startFetch(query: String, save: Boolean) = copy(
        query = query,
        loadState = LoadState.Loading,
        request = FetchRequest(id = (request?.id ?: 0) + 1, query = query),
        pendingSave = if (save) query else null,
    )
}

override fun onTransition(event: WeatherUIEvent, old: WeatherUIState, new: WeatherUIState) {
    if (new.request != old.request) new.request?.let(::fetch)
    if (old.pendingSave != null && new.pendingSave == null && new.loadState is LoadState.Success) save(old.pendingSave)
    if (event is WeatherUIEvent.OnDeleteCacheSearch) delete(event.item)
}
```

## Strengths

- Same files and the same `Reducer` interface as `develop`, with no new concepts.
- Every decision is in the pure reducer, and races are handled with `requestId`.

## Weaknesses

- The side effects are **implicit**. You have to know the rules in `onTransition` to know what a transition triggers.
- "Start or replace something" (a new `request` means fetch) reads well. **One-time actions** like "save once, on success" turn into unclear diff rules (rule 2 above).
- Only one effect per event, because of `Pair<State, Effect?>`.

## Upgrade signal

When `onTransition` fills up with rules like rule 2, move to [04](04-elm-commands.md). Each `if` becomes a `Command` that the reducer returns.
