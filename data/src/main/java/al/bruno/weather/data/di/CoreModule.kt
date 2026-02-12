package al.bruno.weather.data.di

import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single


@Module
@ComponentScan
@Configuration
class CoreModule {
    @Single(createdAtStart = false)
    fun getFusedLocationProviderClient(context: Context): FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
}