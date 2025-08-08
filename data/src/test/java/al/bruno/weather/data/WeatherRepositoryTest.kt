package al.bruno.weather.data

import al.bruno.domain.weather.model.Coord
import al.bruno.domain.weather.model.Result
import al.bruno.domain.weather.model.Weather
import al.bruno.weather.data.network.WeatherNetworkDataSource
import al.bruno.weather.data.network.model.CloudsResponse
import al.bruno.weather.data.network.model.ConditionResponse
import al.bruno.weather.data.network.model.CoordResponse
import al.bruno.weather.data.network.model.MainResponse
import al.bruno.weather.data.network.model.SysResponse
import al.bruno.weather.data.network.model.WeatherResponse
import al.bruno.weather.data.network.model.WindResponse
import al.bruno.weather.data.repository.WeatherRepositoryImpl
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime
import java.time.ZoneOffset

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherRepositoryTest {

    private val mockDataSource = mockk<WeatherNetworkDataSource>()
    private lateinit var repository: WeatherRepositoryImpl

    @Before
    fun setUp() {
        repository = WeatherRepositoryImpl(mockDataSource)
    }

    @Test
    fun `weather returns Result Success`() = runTest {
        coEvery { mockDataSource.weather("piqeras") } returns weatherResponse

        val result = repository.weather("piqeras")

        assertTrue(result is Result.Success)
        assertEquals("Piqeras", (result as Result.Success).data.name)
    }

    @Test
    fun `weather returns Result Error on exception`() = runTest {
        coEvery { mockDataSource.weather("piqeras") } throws RuntimeException("Network error")

        val result = repository.weather("piqeras")

        assertTrue(result is Result.Error)
        assertEquals("Network error", (result as Result.Error).error)
    }
}

val weatherResponse = WeatherResponse(
    coord = CoordResponse(
        lon = 19.8905,
        lat = 40.0163
    ),
    weather = listOf(
        ConditionResponse(
            id = 800,
            main = "Clear",
            description = "clear sky",
            icon = "01d"
        )
    ),
    base = "stations",
    main = MainResponse(
        temp = 300.82,
        feelsLike = 301.57,
        tempMin = 300.82,
        tempMax = 300.82,
        pressure = 1015,
        humidity = 54,
        seaLevel = 1015,
        groundLevel = 994
    ),
    visibility = 10000,
    wind = WindResponse(
        speed = 3.22,
        deg = 276,
        gust = 3.31
    ),
    clouds = CloudsResponse(
        all = 0
    ),
    dt =  LocalDateTime.ofEpochSecond(1754560661, 0, ZoneOffset.UTC),
    sys = SysResponse(
        country = "AL",
        sunrise = LocalDateTime.ofEpochSecond(1754538263, 0, ZoneOffset.UTC),
        sunset = LocalDateTime.ofEpochSecond(1754588885, 0, ZoneOffset.UTC)
    ),
    timezone = 7200,
    id = 783148,
    name = "Piqeras",
    cod = 200
)