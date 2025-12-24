package al.bruno.weather.data.di

import al.bruno.weather.data.serialization.LocalDateTimeSerialization
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.cio.endpoint
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.URLProtocol
import io.ktor.http.encodedPath
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import java.time.LocalDateTime

@Module
@ComponentScan
class NetworkModule {
    @Single(createdAtStart = false)
    fun networkModule(): HttpClient =
        HttpClient(CIO) {
            expectSuccess = true
            engine {
                requestTimeout = 30_000
                endpoint {
                    maxConnectionsPerRoute = 10
                }
            }
            defaultRequest {
                url {
                    protocol = URLProtocol.HTTPS
                    host = "api.openweathermap.org"
                }
            }
            install(Logging) {
                level = LogLevel.ALL
                logger = Logger.ANDROID
            }
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
}