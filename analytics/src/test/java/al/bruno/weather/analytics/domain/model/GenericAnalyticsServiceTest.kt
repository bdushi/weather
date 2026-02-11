package obg.android.feature.analytics.domain.model

import io.kotest.matchers.maps.shouldContain
import io.kotest.matchers.maps.shouldContainExactly
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import org.junit.Before
import org.junit.Test

class GenericAnalyticsServiceTest {

    var eventName: String? = null
    var eventProps: Map<String, String> = emptyMap()
    var redact: Boolean = false

    val prop3Provider = { "p3-dynamic" }
    val prop4Provider = { "p4-dynamic" }

    val testService = object : GenericAnalyticsService(
        defaultProps = {
            mapOf(
                "prop1" to "p1-default",
                "prop2" to "p2-default",
                "prop3" to prop3Provider(),
                "prop4" to prop4Provider()
            )
        },
        redactedProps = setOf("prop2", "prop4", "prop6"),
    ) {
        override val id = ServiceId("TestAnalyticsService")

        override fun initialize() = Unit
        override fun setUserProperty(name: String, value: String) = Unit
        override fun setUserId(id: String) = Unit

        override fun sendNativeEvent(name: String, properties: Map<String, String>) {
            eventName = name
            eventProps = properties
        }
    }

    @Before
    fun setUpTest() {
        eventName = null
        eventProps = emptyMap()
    }

    @Test
    fun `event name is passed`() {
        testService.logEvent("event1", emptyMap())

        eventName shouldBe "event1"
    }

    @Test
    fun `default properties are included when sending event with no props`() {
        testService.logEvent("event2", emptyMap(), false)

        eventProps.shouldContainExactly(
            mapOf(
                "prop1" to "p1-default",
                "prop2" to "p2-default",
                "prop3" to "p3-dynamic",
                "prop4" to "p4-dynamic"
            )
        )
    }

    @Test
    fun `logEvent properties override default ones`() {
        testService.logEvent("event3", mapOf("prop1" to "p1", "prop3" to "p3"))

        eventProps.should {
            it.shouldHaveSize(4)
            it.shouldContain("prop1", "p1")
            it.shouldContain("prop3", "p3")
        }
    }

    @Test
    fun `logEvent properties are combined with default ones`() {
        testService.logEvent("event4", mapOf("prop5" to "p5", "prop6" to "p6"), false)

        eventProps.shouldContainExactly(
            mapOf(
                "prop1" to "p1-default",
                "prop2" to "p2-default",
                "prop3" to "p3-dynamic",
                "prop4" to "p4-dynamic",
                "prop5" to "p5",
                "prop6" to "p6"
            )
        )
    }

    @Test
    fun `logEvent all redacted properties have cleared values whenever redaction is enabled`() {
        redact = true

        testService.logEvent("event5", mapOf("prop5" to "p5", "prop6" to "p6"), redact)

        eventProps.shouldContainExactly(
            mapOf(
                "prop1" to "p1-default",
                "prop2" to "",
                "prop3" to "p3-dynamic",
                "prop4" to "",
                "prop5" to "p5",
                "prop6" to ""
            )
        )
    }
}
