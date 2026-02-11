package obg.android.feature.analytics.domain.model.event

import io.kotest.matchers.maps.shouldContainAll
import io.kotest.matchers.maps.shouldContainExactly
import io.kotest.matchers.shouldBe
import obg.android.feature.analytics.domain.model.ServiceId
import obg.android.feature.analytics.domain.model.TestEvents
import org.junit.Test

class EventConfiguratorImplTest {

    @Test
    fun `unchanged name for empty configuration`() {
        val configurator = configure("event_name") {}

        configurator.buildName(ServiceId("anything")) shouldBe "event_name"
    }

    @Test
    fun `unchanged name for service not used in configuration`() {
        val configurator = configure("name") {
            service(SERVICE_A) {
                name { "prefix_$it" }
            }
        }

        configurator.buildName(SERVICE_B) shouldBe "name"
    }

    @Test
    fun `changed name for configured service`() {
        val configurator = configure("name") {
            service(SERVICE_A) {
                name { "prefixed_$it" }
            }
        }

        configurator.buildName(SERVICE_A) shouldBe "prefixed_name"
    }

    @Test
    fun `empty properties for empty configuration`() {
        val configurator = configure("event_name") {}

        configurator.buildProps(SERVICE_A) shouldBe emptyMap()
    }

    @Test
    fun `buildProps with all common properties set`() {
        val configurator = configure("event_name") {
            set("prop1", "1")
            set("prop2", "2")
        }

        configurator.buildProps(SERVICE_A).shouldContainExactly(
            mapOf(
                "prop1" to "1",
                "prop2" to "2"
            )
        )
    }

    @Test
    fun `buildProps with common and service specific set properties`() {
        val configurator = configure("event_name") {
            service(SERVICE_A) {
                set("prop-a", "A")
            }
            set("prop-1", "1")
        }

        configurator.buildProps(SERVICE_A)
            .shouldContainExactly(
                mapOf(
                    "prop-a" to "A",
                    "prop-1" to "1"
                )
            )
        configurator.buildProps(SERVICE_OTHER)
            .shouldContainExactly(mapOf("prop-1" to "1"))
    }

    @Test
    fun `fixed property value is used before trying property providers`() {
        val configurator = configure("event_name", mapOf("prop-common" to "CONST")) {
            require("prop-common")
        }

        configurator.buildProps(SERVICE_OTHER)
            .shouldContainExactly(
                mapOf("prop-common" to "CONST")
            )
    }

    @Test
    fun `buildProps with common and service specific required properties`() {
        val configurator = configure("event_name") {
            require("prop-common")
            service(SERVICE_A) {
                require("prop-a")
            }
        }

        configurator.buildProps(SERVICE_OTHER)
            .shouldContainExactly(mapOf("prop-common" to "common"))
        configurator.buildProps(SERVICE_A)
            .shouldContainExactly(
                mapOf(
                    "prop-common" to "common",
                    "prop-a" to "A"
                )
            )
    }

    @Test
    fun `buildProps with common and service specific optional properties`() {
        val configurator = configure("event_name") {
            optional("prop-common-present")
            optional("prop-common-absent")
            service(SERVICE_A) {
                optional("prop-a-present")
                optional("prop-a-absent")
            }
        }

        configurator.buildProps(SERVICE_OTHER)
            .shouldContainExactly(
                mapOf("prop-common-present" to "common1")
            )
        configurator.buildProps(SERVICE_A)
            .shouldContainExactly(
                mapOf(
                    "prop-common-present" to "common1",
                    "prop-a-present" to "A1"
                )
            )
    }

    @Test
    fun `sub event overrides base event and includes all optional properties`() {
        val configurator = configureEvent(
            TestEvents.BaseEvent.SubEvent,
            mapOf(
                "sub-required" to "sub_required_1",
                "sub-optional" to "sub_optional",
                "base-required" to "base_required",
                "base-optional" to "base_optional"
            )
        )

        configurator.buildProps(SERVICE_OTHER)
            .shouldContainExactly(
                mapOf(
                    "base-prop" to "sub", // override from base
                    "sub-required" to "sub_required_1",
                    "sub-optional" to "sub_optional",
                    "base-required" to "base_required",
                    "base-optional" to "base_optional"
                )
            )
    }

    @Test
    fun `sub event overrides base event when optional properties are absent`() {
        val configurator = configureEvent(
            TestEvents.BaseEvent.SubEvent,
            mapOf(
                "sub-required" to "sub_required_1",
                "base-optional" to "now_required"
            )
        )

        configurator.buildProps(SERVICE_OTHER)
            .shouldContainExactly(
                mapOf(
                    "base-prop" to "sub",
                    "sub-required" to "sub_required_1",
                    "base-optional" to "now_required"
                )
            )
    }

    @Test
    fun `sub event overrides base event service specific properties`() {
        val configurator = configureEvent(
            TestEvents.BaseEvent.SubEvent,
            mapOf(
                "sub-required" to "sub_required_1",
                "sub-optional" to "sub_optional",
                "base-required" to "base_required",
                "base-optional" to "base_optional",
                "base-a-required" to "base_a_required",
                "base-a-optional" to "base_a_optional"
            )
        )

        configurator.buildProps(SERVICE_A)
            .shouldContainAll(
                mapOf(
                    "base-a-prop" to "sub-a",
                    "sub-a-prop" to "sub-a-new",
                    "base-a-required" to "base_a_required",
                    "base-a-optional" to "base_a_optional"
                )
            )
    }

    @Test
    fun `sub event overrides base event service specific properties when optional ones are absent`() {
        val configurator = configureEvent(
            TestEvents.BaseEvent.SubEvent,
            mapOf(
                "sub-required" to "sub_required_1",
                "base-optional" to "base_optional",
                "base-a-optional" to "base_a_optional"
            )
        )

        configurator.buildProps(SERVICE_A)
            .shouldContainAll(
                mapOf(
                    "base-a-optional" to "base_a_optional"
                )
            )
    }

    @Test
    fun `sub event does not override base event service specific properties when empty`() {
        val configurator = configureEvent(
            TestEvents.BaseEvent.SubEvent,
            mapOf(
                "sub-required" to "sub_required",
                "base-optional" to "base_optional_now_required",
                "base-b-required" to "base_b_required",
                "base-b-optional" to "base_b_optional"
            )
        )

        configurator.buildProps(SERVICE_B)
            .shouldContainAll(
                mapOf(
                    "base-b-prop" to "base_b",
                    "base-b-required" to "base_b_required",
                    "base-b-optional" to "base_b_optional"
                )
            )
    }

    companion object {

        val PROVIDED_PROPS = mapOf(
            "prop-common" to "common",
            "prop-common-present" to "common1",
            "prop-a" to "A",
            "prop-a-present" to "A1"
        )

        val SERVICE_A = ServiceId("service-a")
        val SERVICE_B = ServiceId("service-b")
        val SERVICE_OTHER = ServiceId("service-other")

        private fun configure(
            eventName: String,
            fixedProperties: Map<String, String> = emptyMap(),
            configuration: EventConfigurator.() -> Unit
        ): EventConfiguratorImpl {
            val configurator = EventConfiguratorImpl(eventName, fixedProperties) { propertyName ->
                PROVIDED_PROPS.filter { it.key == propertyName }
            }
            configuration.invoke(configurator)
            return configurator
        }

        private fun configureEvent(
            event: Event,
            providedProperties: Map<String, String> = PROVIDED_PROPS
        ): EventConfiguratorImpl {
            val configurator = EventConfiguratorImpl(event.name) { propertyName ->
                providedProperties.filter { it.key == propertyName }
            }
            event.configuration.invoke(configurator)
            return configurator
        }
    }
}
