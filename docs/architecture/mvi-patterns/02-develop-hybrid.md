# 02. The `develop` hybrid (anti-pattern)

| | |
|---|---|
| Family | A mix: a Redux-style reducer plus Orbit-style direct state writes |
| Who decides | The reducer **and** an overridden `sendEvent` |
| In this repo | `reducer` branch (`4c18cdb` "mvi with reducer", `378a29a`), then `develop` and `analytics` |
| Status | **Replaced** by [04](04-elm-commands.md). Kept so we remember why |

## What it looks like

```kotlin
interface Reducer<State : UiState, Event : UiEvent, Effect : UiEffect> {
    fun reduce(state: State, event: Event): Pair<State, Effect?>
}

override val reducer = object : Reducer<WeatherUIState, WeatherUIEvent, WeatherUIEffect> {
    override fun reduce(state: WeatherUIState, event: WeatherUIEvent) = when (event) {
        is WeatherUIEvent.Search ->
            validate(event.query)?.let { state to WeatherUIEffect.ShowError(it) }
                ?: (state.copy(query = event.query, uIState = UIState.Loading) to null)
        WeatherUIEvent.OnRetry -> state.copy(uIState = UIState.Loading) to null
        is WeatherUIEvent.OnDeleteCacheSearch -> state to null
        // ...
    }
}

override fun sendEvent(event: WeatherUIEvent) {
    super.sendEvent(event)
    when (event) {                                                     // ⚠ second decision point
        is WeatherUIEvent.Search ->
            if (validate(event.query) == null) search(event.query, save = true)   // ⚠ validated twice
        WeatherUIEvent.OnRetry -> search(currentState.query, save = false)
        is WeatherUIEvent.OnDeleteCacheSearch -> delete(event.cacheSearchUiModel)
        // ...
    }
}

private fun search(query: String, save: Boolean) {
    viewModelScope.launch {                                            // ⚠ no cancellation
        try {
            val (w, f) = loadWeather(query)
            setState { copy(uIState = UIState.Success, weatherUiModel = w, forecastUiModel = f) }  // ⚠ skips reducer
            if (save) insertCacheSearchUseCase(CacheSearch(id = 0, query = query))
        } catch (e: Exception) {                                       // ⚠ swallows CancellationException
            setState { copy(uIState = UIState.Error(e.message)) }      // ⚠ skips reducer
        }
    }
}
```

## What's wrong with it

1. **The reducer isn't the single place where state changes.** Only the *start* of each action goes through `reduce()`. Results, the cache observer and the delete rollback all write with `setState {}`. Reducer tests miss most transitions.
2. **There are two `when` blocks per event** (in the reducer and in `sendEvent`), and they have to stay in sync by hand. Validation is already duplicated.
3. **`onStart` runs again.** `onStart { onStart() }` under `SharingStarted.WhileSubscribed(5000)` runs again after more than 5 seconds in the background. That adds another `observeCacheSearches()` collector, which is never cancelled, and refetches the current location, replacing the city the user searched for.
4. **`setState` isn't atomic** (`_state.value = currentState.update()`).
5. **Searches aren't cancelled**, so "Paris" can finish after "Rome" and overwrite it.
6. **Errors are reported twice**, as both `UIState.Error` and the `ShowError` effect.
7. **Effects are collected with `LaunchedEffect(Unit)`**, which isn't lifecycle-aware, and both handlers are commented out.
8. The reducer is an anonymous object inside the ViewModel, so it can't be tested on its own.

## Where it came from

It mixes two traditions:

- From [Orbit](05-orbit.md): impure code that writes state (`reduce {}` there, `setState` here) whenever it wants.
- From Redux-style Android articles: a separate pure `Reducer` that returns `State` (and perhaps one `Effect`), with "async work happens elsewhere".

Most Android MVI tutorials stop at "elsewhere". [03](03-reducer-state-diff.md) and [04](04-elm-commands.md) are two ways to say where that work goes.

## Rule we took from this

> A **hybrid** is any code path that changes state without going through the reducer. The base class must make that impossible: no `setState`, and no `open sendEvent`.
