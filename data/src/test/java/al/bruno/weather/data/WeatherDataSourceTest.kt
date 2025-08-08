package al.bruno.weather.data

import al.bruno.weather.data.serialization.LocalDateTimeSerialization
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondError
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherDataSourceTest {

    private lateinit var httpClient: HttpClient
    private lateinit var dataSource: WeatherDataSource

    @Before
    fun setUp() {
        val mockEngine = MockEngine { request ->
            when {
                request.url.encodedPath.contains("weather") -> respond(
                    content = weatherJson,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )

                request.url.encodedPath.contains("forecast") -> respond(
                    content = forecastJson,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )

                else -> respondError(HttpStatusCode.NotFound)
            }
        }

        httpClient = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true // Ignore missing fields in the JSON
                    prettyPrint = true
                    isLenient = true
                    serializersModule = SerializersModule {
                        contextual(LocalDateTime::class, LocalDateTimeSerialization())
                    }
                })
            }
        }

        dataSource = WeatherDataSource(httpClient)
    }

    @Test
    fun `weather returns parsed WeatherResponse`() = runTest {
        val result = dataSource.weather("piqeras")
        assertEquals("Piqeras", result.name)
        assertEquals("Clear", result.weather.first().main)
    }

    @Test
    fun `forecast returns parsed ForecastResponse`() = runTest {
        val result = dataSource.forecast("piqeras")
        assertEquals("Piqeras", result.city.name)
        assertEquals("Clear", result.list.first().weather.first().main)
    }

    private val weatherJson = """
        {
          "coord": {"lon": 19.8905, "lat": 40.0163},
          "weather": [{"id":800,"main":"Clear","description":"clear sky","icon":"01d"}],
          "base": "stations",
          "main": {"temp":301.23,"feels_like":301.78,"temp_min":301.23,"temp_max":301.23,"pressure":1015,"humidity":51},
          "visibility": 10000,
          "wind": {"speed":1.88,"deg":254},
          "clouds": {"all":0},
          "dt": 1754552161,
          "sys": {"country":"AL","sunrise":1754538263,"sunset":1754588885},
          "timezone": 7200,
          "id": 783148,
          "name": "Piqeras",
          "cod": 200
        }
    """.trimIndent()

    private val forecastJson = """
        {
          "cod": "200",
          "message": 0,
          "cnt": 40,
          "list": [
            {
              "dt": 1754568000,
              "main": {
                "temp": 301.23,
                "feels_like": 301.97,
                "temp_min": 301.23,
                "temp_max": 301.38,
                "pressure": 1015,
                "sea_level": 1015,
                "grnd_level": 993,
                "humidity": 53,
                "temp_kf": -0.15
              },
              "weather": [
                {
                  "id": 800,
                  "main": "Clear",
                  "description": "clear sky",
                  "icon": "01d"
                }
              ],
              "clouds": {
                "all": 1
              },
              "wind": {
                "speed": 2.8,
                "deg": 269,
                "gust": 3.63
              },
              "visibility": 10000,
              "pop": 0,
              "sys": {
                "pod": "d"
              },
              "dt_txt": "2025-08-07 12:00:00"
            },
            {
              "dt": 1754578800,
              "main": {
                "temp": 302.56,
                "feels_like": 303.64,
                "temp_min": 302.56,
                "temp_max": 303.26,
                "pressure": 1014,
                "sea_level": 1014,
                "grnd_level": 993,
                "humidity": 52,
                "temp_kf": -0.7
              },
              "weather": [
                {
                  "id": 800,
                  "main": "Clear",
                  "description": "clear sky",
                  "icon": "01d"
                }
              ],
              "clouds": {
                "all": 5
              },
              "wind": {
                "speed": 1.3,
                "deg": 278,
                "gust": 2.6
              },
              "visibility": 10000,
              "pop": 0,
              "sys": {
                "pod": "d"
              },
              "dt_txt": "2025-08-07 15:00:00"
            }
          ],
          "city": {
            "id": 783148,
            "name": "Piqeras",
            "coord": {
              "lat": 40.0163,
              "lon": 19.8905
            },
            "country": "AL",
            "population": 23437,
            "timezone": 7200,
            "sunrise": 1754538263,
            "sunset": 1754588885
          }
        }
    """.trimIndent()
}