package al.bruno.weather.core.di

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.Module
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Module
@ComponentScan
@Configuration
class DispatchersModule {
    @Single(createdAtStart = false)
    @Named("Main")
    fun mainDispatcher(): CoroutineDispatcher = Dispatchers.Main

    @Single(createdAtStart = false)
    @Named("IO")
    fun ioDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Single(createdAtStart = false)
    @Named("Default")
    fun defaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    @Single(createdAtStart = false)
    @Named("Unconfined")
    fun unconfinedDispatcher(): CoroutineDispatcher = Dispatchers.Unconfined
}