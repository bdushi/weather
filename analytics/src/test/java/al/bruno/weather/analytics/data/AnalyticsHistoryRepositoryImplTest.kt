package obg.android.feature.analytics.data

import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import obg.android.feature.analytics.domain.model.ServiceId
import obg.android.feature.analytics.history.LoggedEvent
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

class AnalyticsHistoryRepositoryImplTest {

    val dateTimeProvider = mockk<() -> LocalDateTime>()

    @Before
    fun setUpTest() {
        every { dateTimeProvider.invoke() } returnsMany listOf(T0, T1, T2)
    }

    @Test
    fun `events are returned ordered from the most recent one`() {
        val repository = AnalyticsHistoryRepositoryImpl(dateTimeProvider)

        repository.appendEvent(SERVICE, "event1", emptyMap())
        repository.appendEvent(SERVICE, "event2", emptyMap())
        repository.appendFailedEvent(SERVICE, "event3", "Missing property")

        repository.events() shouldContainExactly listOf(
            LoggedEvent(T2, SERVICE, "event3", emptyMap(), "Missing property"),
            LoggedEvent(T1, SERVICE, "event2", emptyMap()),
            LoggedEvent(T0, SERVICE, "event1", emptyMap())
        )
    }

    @Test
    fun `after clearing, empty list of events is returned`() {
        val repository = AnalyticsHistoryRepositoryImpl(dateTimeProvider)

        repository.appendEvent(SERVICE, "event1", emptyMap())
        repository.appendEvent(SERVICE, "event2", emptyMap())
        repository.appendEvent(SERVICE, "event3", emptyMap())
        repository.clear()

        repository.events() shouldBe emptyList()
    }

    companion object {
        val START_TIME = LocalDateTime.now()
        val T0 = START_TIME
        val T1 = START_TIME.plusSeconds(1)
        val T2 = START_TIME.plusSeconds(2)

        val SERVICE = ServiceId("Firebase")
    }
}
