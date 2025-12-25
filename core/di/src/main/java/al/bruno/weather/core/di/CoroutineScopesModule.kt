package al.bruno.weather.core.di

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Module
@ComponentScan
class CoroutineScopesModule {
    @Single(createdAtStart = false)
    @Named("MainScope")
    fun mainScope(@Named("Main") mainDispatcher: CoroutineDispatcher): CoroutineScope =
        CoroutineScope(SupervisorJob() + mainDispatcher)

    @Single(createdAtStart = false)
    @Named("IOScope")
    fun ioScope(@Named("IO") ioDispatcher: CoroutineDispatcher): CoroutineScope =
        CoroutineScope(SupervisorJob() + ioDispatcher)

    @Single(createdAtStart = false)
    @Named("DefaultScope")
    fun defaultScope(@Named("Default") defaultDispatcher: CoroutineDispatcher): CoroutineScope =
        CoroutineScope(SupervisorJob() + defaultDispatcher)

    @Single(createdAtStart = false)
    @Named("UnconfinedScope")
    fun unconfinedScope(@Named("Unconfined") unconfinedDispatcher: CoroutineDispatcher): CoroutineScope =
        CoroutineScope(SupervisorJob() + unconfinedDispatcher)
}