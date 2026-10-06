package al.bruno.weather.presentation.weather

import al.bruno.weather.core.viewmodel.Next
import al.bruno.weather.core.viewmodel.Reducer
import al.bruno.weather.presentation.model.LoadState

private typealias WeatherNext = Next<WeatherUIState, WeatherUIEffect, WeatherCommand>

/** The weather screen's complete behavior. Pure: no I/O, no coroutines. */
object WeatherReducer : Reducer<WeatherUIState, WeatherUIEvent, WeatherUIEffect, WeatherCommand> {

    override fun reduce(state: WeatherUIState, event: WeatherUIEvent): WeatherNext = when (event) {

        WeatherUIEvent.Started -> {
            val id = state.requestId + 1
            Next(
                state = state.copy(requestId = id, loadState = LoadState.Loading, pendingSave = null),
                commands = listOf(WeatherCommand.ObserveHistory, WeatherCommand.ResolveLocation(id)),
            )
        }

        is WeatherUIEvent.Search -> {
            val query = event.query.trim()
            when (val error = validate(query)) {
                null -> state.startFetch(query, save = true)
                else -> Next(state, effects = listOf(WeatherUIEffect.ShowError(error)))
            }
        }

        is WeatherUIEvent.OnQueryChange -> Next(state.copy(query = event.query))
        is WeatherUIEvent.OnSelectedItems -> state.startFetch(event.query, save = false)
        WeatherUIEvent.OnClear -> state.startFetch(DEFAULT_QUERY, save = true)
        WeatherUIEvent.OnRetry -> state.startFetch(state.query, save = false)

        is WeatherUIEvent.OnDeleteCacheSearch -> {
            // Optimistic: remove now; DeleteFailed puts it back.
            val remaining = state.cacheSearch.filterNot { it.id == event.item.id }
            Next(
                state = state.copy(cacheSearch = remaining),
                effects = if (remaining.isEmpty()) listOf(WeatherUIEffect.ShowToast("All search history cleared")) else emptyList(),
                commands = listOf(WeatherCommand.DeleteQuery(event.item)),
            )
        }

        // ---- results ----

        is WeatherUIEvent.LocationResolved ->
            if (event.requestId != state.requestId) Next(state)                       // a newer fetch started
            else Next(
                state,
                commands = listOf(
                    WeatherCommand.FetchWeather(event.requestId, WeatherTarget.Coordinates(event.lat, event.lon))
                ),
            )

        is WeatherUIEvent.WeatherLoaded ->
            if (event.requestId != state.requestId) Next(state)                       // stale -> ignore
            else Next(
                state = state.copy(
                    loadState = LoadState.Success,
                    query = event.cityName,
                    weatherUiModel = event.weather,
                    forecastUiModel = event.forecast,
                    pendingSave = null,
                ),
                commands = listOfNotNull(state.pendingSave?.let(WeatherCommand::SaveQuery)),
            )

        is WeatherUIEvent.WeatherFailed ->
            if (event.requestId != state.requestId) Next(state)
            else Next(state.copy(loadState = LoadState.Error(event.message), pendingSave = null))

        is WeatherUIEvent.HistoryChanged -> Next(state.copy(cacheSearch = event.items))

        is WeatherUIEvent.HistoryFailed ->
            Next(state, effects = listOf(WeatherUIEffect.ShowError("Failed to load search history: ${event.message}")))

        is WeatherUIEvent.DeleteFailed -> Next(
            state = state.copy(
                cacheSearch = if (state.cacheSearch.any { it.id == event.item.id }) state.cacheSearch
                else state.cacheSearch + event.item,
            ),
            effects = listOf(WeatherUIEffect.ShowError("Failed to delete search: ${event.message}")),
        )
    }

    /** Starts a new fetch by city name; any in-flight fetch becomes stale. */
    private fun WeatherUIState.startFetch(query: String, save: Boolean): WeatherNext {
        val id = requestId + 1
        return Next(
            state = copy(
                requestId = id,
                query = query,
                loadState = LoadState.Loading,
                pendingSave = if (save) query else null,
            ),
            commands = listOf(WeatherCommand.FetchWeather(id, WeatherTarget.City(query))),
        )
    }
}

/** Returns an error message for an invalid (already trimmed) query, or null if it's valid. */
internal fun validate(query: String): String? = when {
    query.isEmpty() -> "Search query cannot be empty"
    query.length < 2 -> "Search query must be at least 2 characters"
    else -> null
}
