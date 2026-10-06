# analytics

DSL-style analytics and logging. A plain Kotlin library with no DI framework inside; the app wires it up in `app/.../di/AnalyticsModule.kt`.

## Logging

```kotlin
val logging = logging {
    minLevel = LogLevel.DEBUG
    sink(LogcatSink)              // add more sinks: file, crash reporter...
}
private val log = logging.logger("Weather")

log.d { "Loaded $city" }          // the lambda runs only if DEBUG is enabled
log.e(error) { "Fetch failed" }
```

## Analytics

Set up once:

```kotlin
val analytics = analytics {
    logging = appLogging
    service(LoggingAnalyticsService(appLogging))        // or your Firebase/Amplitude service
    properties("app_version") { mapOf("app_version" to BuildConfig.VERSION_NAME) }
    logEvents()                                          // log every sent event at DEBUG
    history(InMemoryAnalyticsHistory())                  // keep recent events, e.g. for a debug screen
    redaction { !BuildConfig.DEBUG }
}
```

Send events inline:

```kotlin
analytics.logEvent("retry_tapped") { set("screen", "weather") }
```

Or define reusable ones:

```kotlin
data class CitySearched(val city: String) : Event {
    override val name = "city_searched"
    override val configuration: EventConfiguration = {
        set("city", city)                  // fixed value
        require("app_version")             // from setProperties() or a provider; the event fails without it
        optional("user_tier")              // sent only if something provides it
        service(FIREBASE) {                // overrides for one backend
            name { "weather_$it" }
            set("source", "search_bar")
        }
    }
}
analytics.logEvent(CitySearched("Paris"))
```

### How an event is resolved

For each service (or only `Event.serviceId`, if set):

1. The **name** is the event name, renamed by that service's `name { }` if present.
2. **`set`** values are used as is. A service-specific value overrides the shared one.
3. **`require` / `optional`** values come from `setProperties(...)` first, then from the properties provider that declares the name. Each provider is asked at most once per event, and keys it didn't declare are dropped.
4. A missing `require` value fails the event *for that service*: it's logged at ERROR and recorded with an error, and the other services still send.
5. The service sends it, blanking `redactedProperties` while `redaction { }` returns true.
6. **Interceptors** (`logEvents()`, `history(...)`, `intercept { }`) see the result, including failures.

`logEvent` never throws: a broken event configuration, provider, service or interceptor is logged instead.

### Adding a real backend

```kotlin
class FirebaseAnalyticsService(private val firebase: FirebaseAnalytics) :
    GenericAnalyticsService(redactedProperties = setOf("email")) {
    override val id = ServiceId("Firebase")
    override fun sendEvent(name: String, properties: Map<String, String>) =
        firebase.logEvent(name, bundleOf(*properties.toList().toTypedArray()))
}
```

Then add `service(FirebaseAnalyticsService(...))` in `AnalyticsModule`.

## Where this came from

The event DSL (`set` / `require` / `optional` / `service { name { } }`), the providers, the per-service routing and the interceptors follow the design of an analytics module from an older project, kept in `analytics_dev/` for reference. Changes from that version:

| Old (`analytics_dev`) | Here |
|---|---|
| Hilt `@Inject`/`@Provides`, and separate dev/release modules | An `analytics { }` builder. The app decides what to install per build type |
| Timber | Our own `logging { }` DSL with lazy messages and pluggable sinks |
| Firebase, AppsFlyer and GTM baked in | Backend-agnostic `AnalyticsService`. A `LoggingAnalyticsService` until a real backend is added |
| `EventLoggingInterceptor` logged only *failed* events (the condition was inverted) | `LoggingInterceptor` logs sent events. Failures are logged once, at ERROR |
| Errors stored `e.stackTrace` (prints an array reference) | Errors store the exception message |
| Mutable fixed properties and an unbounded history list, not thread-safe | `ConcurrentHashMap` and a bounded, synchronized `InMemoryAnalyticsHistory` |
| A service throwing could crash the caller | Each service, the event configuration and each interceptor is isolated. `logEvent` never throws |
| `SearchHistoryUseCase` interface + impl | An `AnalyticsHistory.search(query, services)` extension |
| iGaming-specific events and models (bonuses, BankID, limits...) | Not carried over |
