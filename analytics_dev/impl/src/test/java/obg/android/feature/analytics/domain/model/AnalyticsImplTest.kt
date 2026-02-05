package obg.android.feature.analytics.domain.model

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.maps.shouldContainExactly
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldStartWith
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.mockk.verifyOrder
import obg.android.feature.analytics.domain.model.AnalyticsImpl.Companion.propertyProvider
import obg.android.feature.analytics.domain.model.TestEvents.Companion.SERVICE_A
import obg.android.feature.analytics.domain.model.TestEvents.Companion.SERVICE_B
import obg.android.feature.analytics.domain.model.property.PropertiesProvider
import org.junit.Before
import org.junit.Test

class AnalyticsImplTest {

    val eventNameInServiceA = slot<String>()
    val eventNameInServiceB = slot<String>()
    val propsInServiceA = slot<Map<String, String>>()
    val propsInServiceB = slot<Map<String, String>>()

    val serviceA = mockk<AnalyticsService>(relaxUnitFun = true) {
        every { id } returns SERVICE_A
        every { logEvent(capture(eventNameInServiceA), capture(propsInServiceA)) } returns Unit
    }
    val serviceB = mockk<AnalyticsService>(relaxUnitFun = true) {
        every { id } returns SERVICE_B
        every { logEvent(capture(eventNameInServiceB), capture(propsInServiceB)) } returns Unit
    }
    val propertiesProvider = mockk<PropertiesProvider> {
        every { providedProperties } returns setOf("prop-c1", "prop-c2")
        every { provide() } answers {
            mapOf("prop-c1" to propC, "prop-c2" to propC)
        }
    }
    val extraPropertiesProvider = mockk<PropertiesProvider>()

    var propA: String = "A"
    var propB: String = "B"
    var propC: String = "C"
    var propD: String? = null

    @Before
    fun setUpTest() {
        propA = "A"
        propB = "B"
        propC = "C"
        propD = null
    }

    @Test
    fun `initialize is initializing all services`() {
        val analytics = createTestAnalytics()

        analytics.initialize()

        verify {
            serviceA.initialize()
            serviceB.initialize()
        }
    }

    @Test
    fun `passing provider that doesn't declare any property names throws`() {
        shouldThrow<IllegalArgumentException> {
            AnalyticsImpl(
                emptySet(),
                setOf(propertyProvider { emptyMap() }),
            )
        }.message shouldBe "All providers must declare at least one property"
    }

    @Test
    fun `passing provider that declares an empty property name throws`() {
        shouldThrow<IllegalArgumentException> {
            AnalyticsImpl(
                emptySet(),
                setOf(propertyProvider("prop1", "") { emptyMap() }),
            )
        }.message
            .shouldNotBeNull()
            .should {
                it.shouldStartWith("All declared property names in a provider must not be empty")
                it.shouldContain("prop1")
                it.shouldContain("''")
            }
    }

    @Test
    fun `passing providers that provide same property throws`() {
        shouldThrow<IllegalArgumentException> {
            AnalyticsImpl(
                emptySet(),
                setOf(
                    propertyProvider("prop1", "prop2") { emptyMap() },
                    propertyProvider("prop3", "prop2") { emptyMap() }
                )
            )
        }.message shouldBe "More than one provider declares providing 'prop2'"
    }

    @Test
    fun `properties generated but not declared are ignored`() {
        val provider = propertyProvider("prop1", "prop2") {
            mapOf(
                "prop1" to "1",
                "prop2" to "2",
                "prop3" to "3",
                "prop4" to "4"
            )
        }

        provider.provide().shouldContainExactly(
            mapOf(
                "prop1" to "1",
                "prop2" to "2"
            )
        )
    }

    @Test
    fun `event with no properties is passed to all services`() {
        val analytics = createTestAnalytics()

        analytics.logEvent(TestEvents.NoProps)

        verify {
            serviceA.logEvent("no_props_event", emptyMap())
            serviceB.logEvent("no_props_event", emptyMap())
        }
    }

    @Test
    fun `specific event is sent only to one service`() {
        val analytics = createTestAnalytics()

        analytics.logEvent(TestEvents.OneServiceOnly.NoProps)

        verify { serviceA.logEvent("one_service_no_props_event", emptyMap()) }
        verify(exactly = 0) { serviceB.logEvent(any(), any()) }
    }

    @Test
    fun `single property event is sent to all services`() {
        val analytics = createTestAnalytics()

        analytics.logEvent(TestEvents.SingleProp(3))

        verify {
            serviceA.logEvent("single_prop_event", mapOf("prop-counter" to "3"))
            serviceB.logEvent("single_prop_event", mapOf("prop-counter" to "3"))
        }
    }

    @Test
    fun `required property is added from property provider`() {
        propA = "A-new_value"
        val analytics = createTestAnalytics()

        analytics.logEvent(TestEvents.ProvidedProp)

        verify {
            serviceA.logEvent("provided_prop_event", mapOf("prop-a" to "A-new_value"))
            serviceB.logEvent("provided_prop_event", mapOf("prop-a" to "A-new_value"))
        }
    }

    @Test
    fun `property provider is not called multiple times if required property is already generated`() {
        val analytics = createTestAnalytics()

        analytics.logEvent(TestEvents.TwoPropsFromSameProvider)

        verify(exactly = 1) { propertiesProvider.provide() }
    }

    @Test
    fun `optional property is added when available`() {
        val analytics = createTestAnalytics()

        propD = null
        analytics.logEvent(TestEvents.OptionalProp)
        propD = "D"
        analytics.logEvent(TestEvents.OptionalProp)

        verifyOrder {
            serviceA.logEvent("optional_prop_event", emptyMap())
            serviceA.logEvent("optional_prop_event", mapOf("prop-d" to "D"))
        }
        verifyOrder {
            serviceB.logEvent("optional_prop_event", emptyMap())
            serviceB.logEvent("optional_prop_event", mapOf("prop-d" to "D"))
        }
    }

    @Test
    fun `service specific properties are added`() {
        val analytics = createTestAnalytics()

        analytics.logEvent(TestEvents.ServiceSpecificProps("FB", "AF"))

        verify {
            serviceA.logEvent("service_specific_props_event", mapOf("prop-for-a" to "FB"))
            serviceB.logEvent("service_specific_props_event", mapOf("prop-for-b" to "AF"))
        }
    }

    @Test
    fun `service specific event name changes are applied`() {
        val analytics = createTestAnalytics()

        analytics.logEvent(TestEvents.ServiceSpecificChangedNames)

        eventNameInServiceA.captured shouldBe "prefixed_changed_names_event"
        propsInServiceA.captured shouldBe emptyMap()
        eventNameInServiceB.captured shouldBe "changed_names_event_modified"
        propsInServiceB.captured shouldBe emptyMap()
    }

    @Test
    fun `event with same property declared multiple times uses provided value`() {
        val analytics = createTestAnalytics()

        analytics.logEvent(TestEvents.ReDeclaredProperty("should not be used"))

        verify {
            serviceA.logEvent("re_declared_property_event", mapOf("prop-a" to "A"))
            serviceB.logEvent("re_declared_property_event", mapOf("prop-a" to "A"))
        }
    }

    @Test
    fun `fully featured event`() {
        every { extraPropertiesProvider.providedProperties } returns setOf(
            "prop-common-provided",
            "prop-common-optional",
            "prop-fb-provided",
            "prop-af-provided",
            "something-present",
            "something-absent"
        )
        every { extraPropertiesProvider.provide() } returns mapOf(
            "prop-common-provided" to "common1",
            "prop-common-optional" to "common-present",
            "prop-for-a-provided" to "FB",
            "prop-for-b-provided" to "AF",
            "something-present" to "extra"
        )
        val analytics = createTestAnalytics(extraPropertiesProvider)

        analytics.logEvent(TestEvents.FullyFeatured(0.3f, 0.5f, "common"))

        eventNameInServiceA.captured shouldBe "fully_featured_event_a"
        propsInServiceA.captured.shouldContainExactly(
            mapOf(
                "prop-common" to "COMMON",
                "prop-common-provided" to "common1",
                "prop-common-optional" to "common-present",
                "prop-for-a" to "0.30",
                "prop-for-a-provided" to "FB",
                "something-present" to "extra"
            )
        )
        eventNameInServiceB.captured shouldBe "fully_featured_event_b"
        propsInServiceB.captured.shouldContainExactly(
            mapOf(
                "prop-common" to "COMMON",
                "prop-common-provided" to "common1",
                "prop-common-optional" to "common-present",
                "prop-for-b" to "0.5000",
                "prop-for-b-provided" to "AF",
                "something-present" to "extra"
            )
        )
    }

    @Test
    fun `fixed set properties are used before looking in property providers`() {
        val analytics = createTestAnalytics(
            fixedProperties = mapOf(
                "prop-c1" to "C1-constant",
                "prop-c2" to "C2-constant"
            )
        )

        analytics.logEvent(TestEvents.FixedProperties)

        verify {
            serviceA.logEvent(
                "fixed_prop_event",
                mapOf("prop-c1" to "C1-constant", "prop-c2" to "C2-constant")
            )
        }
        verify(exactly = 0) {
            propertiesProvider.provide()
        }
    }

    @Test
    fun `interceptor is called for all events and all services`() {
        val interceptor = createInterceptor()
        val analytics = createTestAnalytics(interceptors = listOf(interceptor))
        val event1 = TestEvents.NoProps
        val event2 = TestEvents.SingleProp(1)

        analytics.logEvent(event1)
        analytics.logEvent(event2)

        verify {
            interceptor.process(event1.name, emptyMap(), SERVICE_A)
            interceptor.process(event1.name, emptyMap(), SERVICE_B)
            interceptor.process(event2.name, mapOf("prop-counter" to "1"), SERVICE_A)
            interceptor.process(event2.name, mapOf("prop-counter" to "1"), SERVICE_B)
        }
    }

    @Test
    fun `events are pushed through all interceptors`() {
        val interceptor1 = createInterceptor()
        val interceptor2 = createInterceptor()
        val interceptor3 = createInterceptor()
        val analytics = createTestAnalytics(
            interceptors = listOf(interceptor1, interceptor2, interceptor3)
        )
        val event = TestEvents.OneServiceOnly.NoProps

        analytics.logEvent(event)

        verifyOrder {
            interceptor1.process(TestEvents.OneServiceOnly.NoProps.name, emptyMap(), SERVICE_A)
            interceptor2.process(TestEvents.OneServiceOnly.NoProps.name, emptyMap(), SERVICE_A)
            interceptor3.process(TestEvents.OneServiceOnly.NoProps.name, emptyMap(), SERVICE_A)
            serviceA.logEvent(event.name, emptyMap())
        }
    }

    @Test
    fun `events passed to service when there are no interceptors defined`() {
        val analytics = createTestAnalytics()
        val event = TestEvents.OneServiceOnly.NoProps

        analytics.logEvent(event)

        verify {
            serviceA.logEvent(event.name, emptyMap())
        }
    }

    @Test
    fun `user property is set on all analytics services`() {
        val analytics = createTestAnalytics()

        analytics.setUserProperty("status", "guest")

        verify {
            serviceA.setUserProperty("status", "guest")
            serviceB.setUserProperty("status", "guest")
        }
    }

    @Test
    fun `user property is set on specific analytics service only`() {
        val analytics = createTestAnalytics()

        analytics.setUserProperty("status", "guest", SERVICE_A)

        verify {
            serviceA.setUserProperty("status", "guest")
        }
    }

    @Test
    fun `user ID is set for all analytics services`() {
        val analytics = createTestAnalytics()

        analytics.setUserId("abc123")

        verify {
            serviceA.setUserId("abc123")
            serviceB.setUserId("abc123")
        }
    }

    @Test
    fun `user ID is set for one specific analytics service only`() {
        val analytics = createTestAnalytics()

        analytics.setUserId("abc123", SERVICE_B)

        verify {
            serviceB.setUserId("abc123")
        }
    }

    private fun createTestAnalytics(
        additionalProvider: PropertiesProvider? = null,
        interceptors: List<EventsInterceptor> = emptyList(),
        fixedProperties: Map<String, String> = emptyMap()
    ): AnalyticsImpl {
        val providers = setOf(
            propertyProvider("prop-a") { mapOf("prop-a" to propA) },
            propertyProvider("prop-b") { mapOf("prop-b" to propB) },
            propertiesProvider, // prop-c1 and prop-c2
            propertyProvider("prop-d") { propD?.let { mapOf("prop-d" to it) } ?: emptyMap() }
        ) + setOfNotNull(additionalProvider)

        return AnalyticsImpl(
            setOf(serviceA, serviceB),
            providers,
            interceptors
        ).apply {
            if (fixedProperties.isNotEmpty()) {
                setProperties(fixedProperties)
            }
        }
    }

    private fun createInterceptor(): EventsInterceptor =
        mockk {
            every { process(any(), any(), any()) } returns Unit
        }
}
