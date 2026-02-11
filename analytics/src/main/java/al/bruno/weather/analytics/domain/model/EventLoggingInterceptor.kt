package al.bruno.weather.analytics.domain.model

import timber.log.Timber
import javax.inject.Inject

class EventLoggingInterceptor @Inject constructor() : EventsInterceptor {
    override fun process(
        eventName: String,
        properties: Map<String, String>,
        serviceId: ServiceId,
        error: String?
    ) {
        // errors are logged in the AnalyticsImpl already
        if (error != null) {
            Timber
                .tag("Analytics-${serviceId.name}")
                .d("Event '%s', properties %s", eventName, properties)
        }
    }
}
