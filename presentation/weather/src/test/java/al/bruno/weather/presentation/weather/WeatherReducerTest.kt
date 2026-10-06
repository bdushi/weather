package al.bruno.weather.presentation.weather

import al.bruno.weather.core.viewmodel.Next
import al.bruno.weather.presentation.model.CacheSearchUiModel
import al.bruno.weather.presentation.model.ForecastUiModel
import al.bruno.weather.presentation.model.LoadState
import al.bruno.weather.presentation.model.WeatherUiModel
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test

/** Plain JUnit: the reducer is pure, so no coroutines, Koin or fake use cases. Always assert the whole Next. */
class WeatherReducerTest {

    private val weather = mockk<WeatherUiModel>()
    private val forecast = mockk<ForecastUiModel>()
    private val paris = CacheSearchUiModel(id = 1, query = "Paris")
    private val rome = CacheSearchUiModel(id = 2, query = "Rome")

    private fun reduce(state: WeatherUIState, event: WeatherUIEvent) = WeatherReducer.reduce(state, event)

    /** Typed Next, so assertEquals can infer the type arguments. */
    private fun next(
        state: WeatherUIState,
        effects: List<WeatherUIEffect> = emptyList(),
        commands: List<WeatherCommand> = emptyList(),
    ) = Next(state, effects, commands)

    // ---- start ----

    @Test
    fun `started observes history and resolves location`() {
        assertEquals(
            next(
                state = WeatherUIState(requestId = 1, loadState = LoadState.Loading),
                commands = listOf(WeatherCommand.ObserveHistory, WeatherCommand.ResolveLocation(requestId = 1)),
            ),
            reduce(WeatherUIState(), WeatherUIEvent.Started),
        )
    }

    @Test
    fun `resolved location fetches weather by coordinates`() {
        val state = WeatherUIState(requestId = 1)
        assertEquals(
            next(state, commands = listOf(WeatherCommand.FetchWeather(1, WeatherTarget.Coordinates(lat = 41.3, lon = 19.8)))),
            reduce(state, WeatherUIEvent.LocationResolved(requestId = 1, lat = 41.3, lon = 19.8)),
        )
    }

    @Test
    fun `location resolved after the user already searched is ignored`() {
        val state = WeatherUIState(requestId = 2, query = "Paris")
        assertEquals(next(state), reduce(state, WeatherUIEvent.LocationResolved(requestId = 1, lat = 0.0, lon = 0.0)))
    }

    // ---- search ----

    @Test
    fun `valid search trims, starts a fetch and remembers to save`() {
        assertEquals(
            next(
                state = WeatherUIState(query = "Paris", loadState = LoadState.Loading, requestId = 1, pendingSave = "Paris"),
                commands = listOf(WeatherCommand.FetchWeather(1, WeatherTarget.City("Paris"))),
            ),
            reduce(WeatherUIState(), WeatherUIEvent.Search("  Paris ")),
        )
    }

    @Test
    fun `blank search shows an error and does not fetch`() {
        val state = WeatherUIState()
        assertEquals(
            next(state, effects = listOf(WeatherUIEffect.ShowError("Search query cannot be empty"))),
            reduce(state, WeatherUIEvent.Search("   ")),
        )
    }

    @Test
    fun `one-character search shows an error and does not fetch`() {
        val state = WeatherUIState()
        assertEquals(
            next(state, effects = listOf(WeatherUIEffect.ShowError("Search query must be at least 2 characters"))),
            reduce(state, WeatherUIEvent.Search("P")),
        )
    }

    @Test
    fun `selecting a history item fetches without saving`() {
        assertEquals(
            next(
                state = WeatherUIState(query = "Rome", loadState = LoadState.Loading, requestId = 1),
                commands = listOf(WeatherCommand.FetchWeather(1, WeatherTarget.City("Rome"))),
            ),
            reduce(WeatherUIState(), WeatherUIEvent.OnSelectedItems("Rome")),
        )
    }

    @Test
    fun `clear fetches and saves the default city`() {
        assertEquals(
            next(
                state = WeatherUIState(query = DEFAULT_QUERY, loadState = LoadState.Loading, requestId = 1, pendingSave = DEFAULT_QUERY),
                commands = listOf(WeatherCommand.FetchWeather(1, WeatherTarget.City(DEFAULT_QUERY))),
            ),
            reduce(WeatherUIState(query = "Paris"), WeatherUIEvent.OnClear),
        )
    }

    @Test
    fun `retry refetches the current query without saving`() {
        val failed = WeatherUIState(query = "Paris", requestId = 3, loadState = LoadState.Error("boom"))
        assertEquals(
            next(
                state = failed.copy(requestId = 4, loadState = LoadState.Loading),
                commands = listOf(WeatherCommand.FetchWeather(4, WeatherTarget.City("Paris"))),
            ),
            reduce(failed, WeatherUIEvent.OnRetry),
        )
    }

    @Test
    fun `query change only updates the query`() {
        assertEquals(next(WeatherUIState(query = "Pa")), reduce(WeatherUIState(), WeatherUIEvent.OnQueryChange("Pa")))
    }

    // ---- results ----

    @Test
    fun `successful load shows the weather and saves the pending query`() {
        val loading = WeatherUIState(query = "paris", requestId = 1, pendingSave = "paris")
        assertEquals(
            next(
                state = WeatherUIState(
                    query = "Paris",
                    weatherUiModel = weather,
                    forecastUiModel = forecast,
                    loadState = LoadState.Success,
                    requestId = 1,
                ),
                commands = listOf(WeatherCommand.SaveQuery("paris")),
            ),
            reduce(loading, WeatherUIEvent.WeatherLoaded(1, "Paris", weather, forecast)),
        )
    }

    @Test
    fun `successful load without a pending save issues no command`() {
        val next = reduce(WeatherUIState(requestId = 1), WeatherUIEvent.WeatherLoaded(1, "Rome", weather, forecast))
        assertEquals(emptyList<WeatherCommand>(), next.commands)
    }

    @Test
    fun `failed load shows the error and drops the pending save`() {
        val loading = WeatherUIState(requestId = 1, pendingSave = "Paris")
        assertEquals(
            next(WeatherUIState(requestId = 1, loadState = LoadState.Error("Not found"))),
            reduce(loading, WeatherUIEvent.WeatherFailed(1, "Not found")),
        )
    }

    @Test
    fun `stale results are ignored`() {
        val state = WeatherUIState(requestId = 2, loadState = LoadState.Loading, pendingSave = "Rome")
        assertEquals(next(state), reduce(state, WeatherUIEvent.WeatherLoaded(1, "Paris", weather, forecast)))
        assertEquals(next(state), reduce(state, WeatherUIEvent.WeatherFailed(1, "boom")))
    }

    @Test
    fun `Paris then Rome - only Rome is shown and saved`() {
        val afterParis = reduce(WeatherUIState(), WeatherUIEvent.Search("Paris")).state
        val afterRome = reduce(afterParis, WeatherUIEvent.Search("Rome")).state
        val parisArrivesLate = reduce(afterRome, WeatherUIEvent.WeatherLoaded(1, "Paris", weather, forecast))
        val romeArrives = reduce(parisArrivesLate.state, WeatherUIEvent.WeatherLoaded(2, "Rome", weather, forecast))

        assertEquals(next(afterRome), parisArrivesLate)
        assertEquals("Rome", romeArrives.state.query)
        assertEquals(listOf(WeatherCommand.SaveQuery("Rome")), romeArrives.commands)
    }

    // ---- history ----

    @Test
    fun `history changes replace the list`() {
        assertEquals(
            next(WeatherUIState(cacheSearch = listOf(paris, rome))),
            reduce(WeatherUIState(cacheSearch = listOf(paris)), WeatherUIEvent.HistoryChanged(listOf(paris, rome))),
        )
    }

    @Test
    fun `history failure shows an error`() {
        val state = WeatherUIState()
        assertEquals(
            next(state, effects = listOf(WeatherUIEffect.ShowError("Failed to load search history: disk full"))),
            reduce(state, WeatherUIEvent.HistoryFailed("disk full")),
        )
    }

    @Test
    fun `deleting an item removes it and asks the executor to delete it`() {
        assertEquals(
            next(
                state = WeatherUIState(cacheSearch = listOf(rome)),
                commands = listOf(WeatherCommand.DeleteQuery(paris)),
            ),
            reduce(WeatherUIState(cacheSearch = listOf(paris, rome)), WeatherUIEvent.OnDeleteCacheSearch(paris)),
        )
    }

    @Test
    fun `deleting the last item also shows a toast`() {
        assertEquals(
            next(
                state = WeatherUIState(cacheSearch = emptyList()),
                effects = listOf(WeatherUIEffect.ShowToast("All search history cleared")),
                commands = listOf(WeatherCommand.DeleteQuery(paris)),
            ),
            reduce(WeatherUIState(cacheSearch = listOf(paris)), WeatherUIEvent.OnDeleteCacheSearch(paris)),
        )
    }

    @Test
    fun `failed delete puts the item back and shows an error`() {
        assertEquals(
            next(
                state = WeatherUIState(cacheSearch = listOf(rome, paris)),
                effects = listOf(WeatherUIEffect.ShowError("Failed to delete search: locked")),
            ),
            reduce(WeatherUIState(cacheSearch = listOf(rome)), WeatherUIEvent.DeleteFailed(paris, "locked")),
        )
    }

    @Test
    fun `failed delete does not duplicate an item the history already restored`() {
        val state = WeatherUIState(cacheSearch = listOf(paris))
        assertEquals(
            next(state, effects = listOf(WeatherUIEffect.ShowError("Failed to delete search: locked"))),
            reduce(state, WeatherUIEvent.DeleteFailed(paris, "locked")),
        )
    }
}
