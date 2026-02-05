package obg.android.feature.analytics.extension

import obg.android.feature.analytics.domain.model.Analytics
import obg.android.feature.analytics.domain.model.event.Event

fun Analytics.logEvents(vararg events: Event) {
    events.forEach(::logEvent)
}
