package obg.android.feature.analytics


abstract class OBGAnalyticsProviderFactory {
    /**
     * Overrides in each platform package (Navitaire/Amadeus)
     */
    abstract fun generateProviders(): List<OBGAnalyticsProvider>
}
