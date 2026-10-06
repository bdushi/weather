package al.bruno.weather.core.analytics

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class AnalyticsTest {

    private class FakeService(name: String, redacted: Set<String> = emptySet()) :
        GenericAnalyticsService(redactedProperties = redacted) {
        override val id = ServiceId(name)
        val sent = mutableListOf<Pair<String, Map<String, String>>>()
        override fun sendEvent(name: String, properties: Map<String, String>) {
            sent += name to properties
        }
    }

    private val firebase = FakeService("Firebase")
    private val amplitude = FakeService("Amplitude")
    private val processed = mutableListOf<ProcessedEvent>()

    private fun analyticsWith(extra: AnalyticsBuilder.() -> Unit = {}) = analytics {
        service(firebase)
        service(amplitude)
        intercept { processed += it }
        redaction { false }
        extra()
    }

    @Test
    fun `fixed values go to every service`() {
        analyticsWith().logEvent("city_searched") { set("city", "Paris"); set("results", 3); set("cached", true) }

        val expected = "city_searched" to mapOf("city" to "Paris", "results" to "3", "cached" to "true")
        assertEquals(listOf(expected), firebase.sent)
        assertEquals(listOf(expected), amplitude.sent)
    }

    @Test
    fun `service block renames and adds properties for that service only`() {
        analyticsWith().logEvent("city_searched") {
            set("city", "Paris")
            service(firebase.id) {
                name { "weather_$it" }
                set("source", "search_bar")
            }
        }

        assertEquals(listOf("weather_city_searched" to mapOf("city" to "Paris", "source" to "search_bar")), firebase.sent)
        assertEquals(listOf("city_searched" to mapOf("city" to "Paris")), amplitude.sent)
    }

    @Test
    fun `service-specific value overrides the shared one`() {
        analyticsWith().logEvent("e") {
            set("screen", "weather")
            service(amplitude.id) { set("screen", "Weather Screen") }
        }

        assertEquals(mapOf("screen" to "weather"), firebase.sent.single().second)
        assertEquals(mapOf("screen" to "Weather Screen"), amplitude.sent.single().second)
    }

    @Test
    fun `event with a serviceId goes only to that service`() {
        analyticsWith().logEvent(event("only_amplitude", serviceId = amplitude.id))

        assertTrue(firebase.sent.isEmpty())
        assertEquals("only_amplitude", amplitude.sent.single().first)
    }

    @Test
    fun `required properties come from fixed properties first, then providers`() {
        val analytics = analyticsWith {
            properties("app_version", "locale") { mapOf("app_version" to "from-provider", "locale" to "en") }
        }
        analytics.setProperties(mapOf("app_version" to "1.2.3"))

        analytics.logEvent("app_open") { require("app_version"); require("locale") }

        assertEquals(mapOf("app_version" to "1.2.3", "locale" to "en"), firebase.sent.single().second)
    }

    @Test
    fun `a provider is asked at most once per event`() {
        var calls = 0
        val analytics = analyticsWith {
            properties("a", "b") { calls++; mapOf("a" to "1", "b" to "2") }
        }

        analytics.logEvent("e") { require("a"); require("b") }   // two services, two properties

        assertEquals(1, calls)
    }

    @Test
    fun `optional properties without a value are left out`() {
        analyticsWith().logEvent("e") { set("x", "1"); optional("user_tier") }

        assertEquals(mapOf("x" to "1"), firebase.sent.single().second)
    }

    @Test
    fun `missing required property fails that event without throwing`() {
        analyticsWith().logEvent("e") { require("missing") }

        assertTrue(firebase.sent.isEmpty())
        assertTrue(amplitude.sent.isEmpty())
        assertEquals(2, processed.size)
        processed.forEach { assertNotNull(it.error) }
    }

    @Test
    fun `a failure in one service does not stop the others`() {
        val broken = object : AnalyticsService {
            override val id = ServiceId("Broken")
            override fun logEvent(name: String, properties: Map<String, String>, isRedactionEnabled: Boolean) =
                throw IllegalArgumentException("name too long")
        }
        val analytics = analytics { service(broken); service(firebase); intercept { processed += it } }

        analytics.logEvent("e")

        assertEquals(1, firebase.sent.size)
        assertEquals("name too long", processed.first { it.serviceId == broken.id }.error)
        assertNull(processed.first { it.serviceId == firebase.id }.error)
    }

    @Test
    fun `a throwing configuration or interceptor never crashes the caller`() {
        val analytics = analyticsWith { intercept { error("boom") } }

        analytics.logEvent("bad") { error("bug in event") }
        analytics.logEvent("good")

        assertEquals(listOf("good"), firebase.sent.map { it.first })
    }

    @Test
    fun `undeclared provider keys are dropped`() {
        val analytics = analyticsWith { properties("a") { mapOf("a" to "1", "sneaky" to "x") } }

        analytics.logEvent("e") { require("a"); optional("sneaky") }

        assertEquals(mapOf("a" to "1"), firebase.sent.single().second)
    }

    @Test
    fun `redaction blanks listed properties while enabled`() {
        val service = FakeService("Redacting", redacted = setOf("email"))
        var redact = true
        val analytics = analytics { service(service); redaction { redact } }

        analytics.logEvent("signed_in") { set("email", "a@b.c"); set("method", "google") }
        redact = false
        analytics.logEvent("signed_in") { set("email", "a@b.c"); set("method", "google") }

        assertEquals(mapOf("email" to "", "method" to "google"), service.sent[0].second)
        assertEquals(mapOf("email" to "a@b.c", "method" to "google"), service.sent[1].second)
    }

    @Test
    fun `user properties and ids are routed by service`() {
        val calls = mutableListOf<String>()
        fun recording(name: String) = object : AnalyticsService {
            override val id = ServiceId(name)
            override fun logEvent(name: String, properties: Map<String, String>, isRedactionEnabled: Boolean) = Unit
            override fun setUserProperty(name: String, value: String) { calls += "${id.name}:$name=$value" }
            override fun setUserId(id: String) { calls += "${this.id.name}:id" }
        }
        val analytics = analytics { service(recording("A")); service(recording("B")) }

        analytics.setUserProperty("tier", "pro", ServiceId("B"))
        analytics.setUserId("42")

        assertEquals(listOf("B:tier=pro", "A:id", "B:id"), calls)
    }

    @Test
    fun `invalid setups fail fast at build time`() {
        assertThrows(IllegalArgumentException::class.java) { analytics { service(firebase); service(FakeService("Firebase")) } }
        assertThrows(IllegalArgumentException::class.java) {
            analytics { properties("a") { emptyMap() }; properties("a") { emptyMap() } }.logEvent("e")
        }
        assertThrows(IllegalArgumentException::class.java) { analytics { properties { emptyMap() } }.logEvent("e") }
    }
}
