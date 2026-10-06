# 01. Option A: event handler

| | |
|---|---|
| Family | Impure handler (the same family as Orbit, Mavericks and Ballast) |
| Who decides | `handleEvent` in the ViewModel (impure) |
| In this repo | Commit `e168a4c` ("Implement MVI BaseViewModel"), deleted in `378a29a` |
| Status | **Not chosen.** Kept for reference |

## Idea

Each UI event goes to one `handleEvent`. That function decides what to do *and* does it: it calls use cases, then `setState { copy(...) }` and `setEffect { ... }`.

```kotlin
fun sendEvent(event: Event) { viewModelScope.launch { handleEvent(event) } }
protected abstract suspend fun handleEvent(event: Event)
```

## Weather example

```kotlin
class WeatherViewModel(...) : BaseViewModel<WeatherUIEvent, WeatherUIState, WeatherUIEffect>(WeatherUIState()) {

    private var searchJob: Job? = null

    override suspend fun handleEvent(event: WeatherUIEvent) {
        when (event) {
            is WeatherUIEvent.Search -> {
                validate(event.query)?.let { setEffect { WeatherUIEffect.ShowError(it) }; return }
                search(event.query, save = true)
            }
            WeatherUIEvent.OnRetry -> search(currentState.query, save = false)
            is WeatherUIEvent.OnDeleteCacheSearch -> delete(event.item)
        }
    }

    private fun search(query: String, save: Boolean) {
        searchJob?.cancel()                                        // new search cancels old
        searchJob = viewModelScope.launch {
            setState { copy(query = query, loadState = LoadState.Loading) }
            try {
                val (w, f) = loadWeather(query)
                setState { copy(loadState = LoadState.Success, weatherUiModel = w, forecastUiModel = f) }
                if (save) insertCacheSearchUseCase(CacheSearch(id = 0, query = query))   // only on success
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                setState { copy(loadState = LoadState.Error(e.message)) }
            }
        }
    }

    private suspend fun delete(item: CacheSearchUiModel) {
        try {
            deleteCacheSearchUseCase(CacheSearch(id = item.id, query = item.query))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            setEffect { WeatherUIEffect.ShowError(e.message ?: "Failed to delete") }
        }
    }
}
```

## Strengths

- Reads top to bottom, with each decision next to the code it affects.
- Few concepts: State, Event and Effect.
- Async flow is easy to write: "save only on success" is a line after the success branch.

## Weaknesses

- State transitions are mixed into coroutines, so you test them through the ViewModel with fake use cases and coroutine test tooling.
- A coroutine per event means two events can interleave.
- Races (one search overtaking another) need a hand-written fix on every screen.
- `setState` has to use `_state.update {}`. The original `_state.value = currentState.update()` loses updates when two writers race.

## Lessons kept

- Rethrow `CancellationException` in every `catch (e: Exception)`.
- One `when` per event: the decision logic for an event lives in one place. The pure reducer keeps this property.
