package obg.android.feature.analytics.di

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import obg.android.feature.analytics.data.AnalyticsHistoryRepositoryImpl
import obg.android.feature.analytics.domain.model.Analytics
import obg.android.feature.analytics.domain.model.AnalyticsImpl
import obg.android.feature.analytics.domain.model.AnalyticsService
import obg.android.feature.analytics.domain.model.AppsFlyerAnalyticsService
import obg.android.feature.analytics.domain.model.FirebaseAnalyticsService
import obg.android.feature.analytics.domain.model.property.PropertiesProvider
import obg.android.feature.analytics.domain.model.property.provider.AppsFlyerPropertiesProvider
import obg.android.feature.analytics.domain.model.property.provider.LocalePropertiesProvider
import obg.android.feature.analytics.domain.model.property.provider.OneAppSingletonPropertiesProvider
import obg.android.feature.analytics.domain.usecase.SearchHistoryUseCase
import obg.android.feature.analytics.domain.usecase.SearchHistoryUseCaseImpl
import obg.android.feature.analytics.history.AnalyticsHistoryRepository
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
interface AnalyticsModule {
    @Binds
    @Singleton
    fun bindAnalytics(impl: AnalyticsImpl): Analytics

    @Binds
    @Singleton
    fun bindAnalyticsHistoryRepository(impl: AnalyticsHistoryRepositoryImpl): AnalyticsHistoryRepository

    @Binds
    @Singleton
    fun bindSearchHistoryUseCase(impl: SearchHistoryUseCaseImpl): SearchHistoryUseCase

    companion object {

        @Provides
        @Singleton
        @IntoSet
        fun provideFirebaseAnalyticsService(
            @ApplicationContext context: Context
        ): AnalyticsService = FirebaseAnalyticsService(context)

        @Provides
        @Singleton
        @IntoSet
        fun provideAppsFlyerAnalyticsService(
            @ApplicationContext context: Context
        ): AnalyticsService = AppsFlyerAnalyticsService(context)

        @Provides
        @Singleton
        @IntoSet
        fun provideAppsFlyerPropertiesProvider(
            @ApplicationContext context: Context
        ): PropertiesProvider = AppsFlyerPropertiesProvider(context)

        @Provides
        @Singleton
        @IntoSet
        fun provideLocalePropertiesProvider(
            impl: LocalePropertiesProvider
        ): PropertiesProvider = impl

        @Provides
        @Singleton
        @IntoSet
        fun provideOneAppSingletonPropertiesProvider(
            impl: OneAppSingletonPropertiesProvider
        ): PropertiesProvider = impl
    }
}
