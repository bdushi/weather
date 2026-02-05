package obg.android.feature.analytics.domain.model

import obg.android.feature.analytics.domain.model.event.Event
import obg.android.feature.analytics.domain.model.event.EventConfigurator

sealed class TestEvents(
    override val name: String,
    override val configuration: EventConfigurator.() -> Unit = {}
) : Event {
    data object NoProps : TestEvents("no_props_event")

    data class SingleProp(val counter: Int) : TestEvents(
        "single_prop_event",
        {
            set("prop-counter", counter.toString())
        }
    )

    data object ProvidedProp : TestEvents(
        "provided_prop_event",
        {
            require("prop-a")
        }
    )

    data object OptionalProp : TestEvents(
        "optional_prop_event",
        {
            optional("prop-d")
        }
    )

    data object TwoPropsFromSameProvider : TestEvents(
        "two_props_same_provider_event",
        {
            require("prop-c1")
            require("prop-c2")
        }
    )

    data class ServiceSpecificProps(val forA: String, val forB: String) : TestEvents(
        "service_specific_props_event",
        {
            service(SERVICE_A) {
                set("prop-for-a", forA)
            }
            service(SERVICE_B) {
                set("prop-for-b", forB)
            }
        }
    )

    data object ServiceSpecificChangedNames : TestEvents(
        "changed_names_event",
        {
            service(SERVICE_A) {
                name { "prefixed_$it" }
            }
            service(SERVICE_B) {
                name { "${it}_modified" }
            }
        }
    )

    data class ReDeclaredProperty(val value: String) : TestEvents(
        "re_declared_property_event",
        {
            set("prop-a", value)
            service(SERVICE_A) {
                optional("prop-a")
            }
            service(SERVICE_B) {
                require("prop-a")
            }
        }
    )

    data object FixedProperties : TestEvents(
        "fixed_prop_event",
        {
            service(SERVICE_A) {
                require("prop-c1")
                optional("prop-c2")
            }
        }
    )

    data class FullyFeatured(
        val fbProp: Float,
        val afProp: Float,
        val commonProp: String
    ) : TestEvents(
        "fully_featured_event",
        {
            set("prop-common", commonProp.uppercase())
            require("prop-common-provided")
            optional("prop-common-optional")
            optional("anything-absent")

            service(SERVICE_A) {
                name { "${it}_a" }
                set("prop-for-a", "%.2f".format(fbProp))
                require("prop-for-a-provided")
                optional("something-present")
                optional("something-absent")
            }

            service(SERVICE_B) {
                name { "${it}_b" }
                set("prop-for-b", "%.4f".format(afProp))
                require("prop-for-b-provided")
                optional("something-present")
                optional("something-absent")
            }
        }
    )

    sealed class OneServiceOnly(name: String) : TestEvents(name) {
        override val serviceId: ServiceId? = SERVICE_A

        data object NoProps : OneServiceOnly("one_service_no_props_event")
    }

    /**
     * Scenario when sub event alters or extends base event definition.
     */
    sealed class BaseEvent(name: String) : TestEvents(name) {
        val baseConfiguration: EventConfigurator.() -> Unit = {
            set("base-prop", "base")
            require("base-required")
            optional("base-optional")

            service(SERVICE_A) {
                set("base-a-prop", "base_a")
                require("base-a-required")
                optional("base-a-optional")
            }

            service(SERVICE_B) {
                set("base-b-prop", "base_b")
                require("base-b-required")
                optional("base-b-optional")
            }
        }

        data object SubEvent : BaseEvent("sub_event") {
            override val configuration: EventConfigurator.() -> Unit = {
                baseConfiguration()

                set("base-prop", "sub") // override
                require("sub-required") // added
                optional("sub-optional") // added
                optional("base-required") // override
                require("base-optional") // override

                service(SERVICE_A) {
                    set("base-a-prop", "sub-a") // override
                    set("sub-a-prop", "sub-a-new") // added
                    optional("base-a-required") // override
                    require("base-a-optional") // override
                }

                service(SERVICE_B) {}
            }
        }
    }

    companion object {
        val SERVICE_A = ServiceId("service-a")
        val SERVICE_B = ServiceId("service-b")
    }
}
