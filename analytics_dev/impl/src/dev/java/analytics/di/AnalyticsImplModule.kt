package analytics.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import obg.android.feature.analytics.domain.model.AnalyticsImpl
import obg.android.feature.analytics.domain.model.AnalyticsService
import obg.android.feature.analytics.domain.model.EventHistoryInterceptor
import obg.android.feature.analytics.domain.model.EventLoggingInterceptor
import obg.android.feature.analytics.domain.model.property.PropertiesProvider
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AnalyticsImplModule {

    @Provides
    @Singleton
    fun provideAnalyticsImplementation(
        analyticsServices: Set<@JvmSuppressWildcards AnalyticsService>,
        propertyProviders: Set<@JvmSuppressWildcards PropertiesProvider>,
        eventHistoryInterceptor: EventHistoryInterceptor,
        eventLoggingInterceptor: EventLoggingInterceptor
    ): AnalyticsImpl =
        AnalyticsImpl(
            analyticsServices,
            propertyProviders,
            listOf(eventHistoryInterceptor, eventLoggingInterceptor)
        )
}
