package obg.android.feature.analytics.domain.model.event

@EventMarker
interface ServiceSpecificEventConfigurator : PropertyConfigurator {
    /**
     * Modify the name of the event.
     */
    fun name(transformer: (String) -> String)
}
