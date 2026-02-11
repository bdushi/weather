package obg.android.feature.analytics.domain.usecase

import io.kotest.matchers.collections.shouldContainExactly
import io.mockk.every
import io.mockk.mockk
import obg.android.feature.analytics.domain.model.Analytics
import obg.android.feature.analytics.history.AnalyticsHistoryRepository
import obg.android.feature.analytics.history.LoggedEvent
import org.junit.Test
import java.time.LocalDateTime

class SearchHistoryUseCaseTest {

    private val repository = mockk<AnalyticsHistoryRepository>(relaxUnitFun = true) {
        every { events() } returns HISTORY
    }

    @Test
    fun `when empty query and service filter, all results are returned`() {
        val useCase = SearchHistoryUseCaseImpl(repository)

        val result = useCase("")

        result shouldContainExactly HISTORY
    }

    @Test
    fun `when service filter is set and empty query, only service specific events are returned`() {
        val useCase = SearchHistoryUseCaseImpl(repository)

        val result = useCase("", setOf(Analytics.APPS_FLYER))

        result shouldContainExactly listOf(E3, E2)
    }

    @Test
    fun `search results when query matches event name exactly`() {
        val useCase = SearchHistoryUseCaseImpl(repository)

        val result = useCase("click")

        result shouldContainExactly listOf(E5)
    }

    @Test
    fun `search results when query matches event name case insensitive`() {
        val useCase = SearchHistoryUseCaseImpl(repository)

        val result = useCase("dEEP_lInk")

        result shouldContainExactly listOf(E3)
    }

    @Test
    fun `search results when query matches event name partially`() {
        val useCase = SearchHistoryUseCaseImpl(repository)

        val result = useCase("star")

        result shouldContainExactly listOf(E2, E1)
    }

    @Test
    fun `search results when query matches property name`() {
        val useCase = SearchHistoryUseCaseImpl(repository)

        val result = useCase("tYp")

        result shouldContainExactly listOf(E5)
    }

    @Test
    fun `search results when query matches property value`() {
        val useCase = SearchHistoryUseCaseImpl(repository)

        val result = useCase("Example")

        result shouldContainExactly listOf(E3)
    }

    @Test
    fun `search results when query matches event name and property name`() {
        val useCase = SearchHistoryUseCaseImpl(repository)

        val result = useCase("ee")

        result shouldContainExactly listOf(E4, E3)
    }

    @Test
    fun `search has no effect when query is shorter than 2 characters`() {
        val useCase = SearchHistoryUseCaseImpl(repository)

        val result = useCase("s")

        result shouldContainExactly HISTORY
    }

    @Test
    fun `no results when nothing matches the query`() {
        val useCase = SearchHistoryUseCaseImpl(repository)

        val result = useCase("no-such-thing")

        result shouldContainExactly emptyList()
    }

    companion object {
        private val NOW: LocalDateTime = LocalDateTime.now()
        val E5 = LoggedEvent(NOW, Analytics.FIREBASE, "click", mapOf("type" to "login"))
        val E4 = LoggedEvent(NOW.minusSeconds(1), Analytics.FIREBASE, "view", mapOf("screen" to "Home"))
        val E3 = LoggedEvent(NOW.minusSeconds(2), Analytics.APPS_FLYER, "Deep_Link", mapOf("url" to "example.com"))
        val E2 = LoggedEvent(NOW.minusSeconds(3), Analytics.APPS_FLYER, "app_started", emptyMap())
        val E1 = LoggedEvent(NOW.minusSeconds(4), Analytics.FIREBASE, "app_started", emptyMap())
        val HISTORY = listOf(E5, E4, E3, E2, E1)
    }
}
