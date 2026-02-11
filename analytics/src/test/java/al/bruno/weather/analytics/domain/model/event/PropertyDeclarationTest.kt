package obg.android.feature.analytics.domain.model.event

import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import obg.android.feature.analytics.domain.model.ServiceId
import obg.android.feature.analytics.domain.model.event.PropertyDeclaration.Spec
import org.junit.Test

class PropertyDeclarationTest {
    @Test
    fun `when no service specific spec, general spec is used`() {
        val declaration = PropertyDeclaration.ofValue("abc")

        declaration.specForService(SERVICE_A) shouldBe Spec.OfValue("abc")
    }

    @Test
    fun `service specific spec takes precedence over general spec`() {
        val declaration = PropertyDeclaration.optional() +
            PropertyDeclaration.required(SERVICE_A)

        declaration.specForService(SERVICE_A) shouldBe Spec.Required
    }

    @Test
    fun `different services can have different specs`() {
        val declaration = PropertyDeclaration.ofValue("a", SERVICE_A) +
            PropertyDeclaration.required(SERVICE_B) +
            PropertyDeclaration.optional(SERVICE_C)

        declaration.specForService(SERVICE_A) shouldBe Spec.OfValue("a")
        declaration.specForService(SERVICE_B) shouldBe Spec.Required
        declaration.specForService(SERVICE_C) shouldBe Spec.Optional
    }

    @Test
    fun `2nd declaration ofValue overwrites previous value`() {
        val declaration = PropertyDeclaration.ofValue("a") +
            PropertyDeclaration.ofValue("b")

        declaration.specForService(SERVICE_A) shouldBe Spec.OfValue("b")
    }

    @Test
    fun `null spec returned when no general declaration and no service match`() {
        val declaration = PropertyDeclaration.required(SERVICE_A)

        declaration.specForService(SERVICE_B).shouldBeNull()
    }

    companion object {
        val SERVICE_A = ServiceId("service-a")
        val SERVICE_B = ServiceId("service-b")
        val SERVICE_C = ServiceId("service-c")
    }
}
