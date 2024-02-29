package se.montrima.fhir.server.configuration

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.support.DefaultProfileValidationSupport
import ca.uhn.fhir.rest.openapi.OpenApiInterceptor
import ca.uhn.fhir.rest.server.interceptor.RequestValidatingInterceptor
import ca.uhn.fhir.spring.boot.autoconfigure.FhirRestfulServerCustomizer
import ca.uhn.fhir.validation.ResultSeverityEnum
import org.hl7.fhir.common.hapi.validation.support.CachingValidationSupport
import org.hl7.fhir.common.hapi.validation.support.CommonCodeSystemsTerminologyService
import org.hl7.fhir.common.hapi.validation.support.InMemoryTerminologyServerValidationSupport
import org.hl7.fhir.common.hapi.validation.support.NpmPackageValidationSupport
import org.hl7.fhir.common.hapi.validation.support.ValidationSupportChain
import org.hl7.fhir.common.hapi.validation.validator.FhirInstanceValidator
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class FhirServerConfig(
    val fhirContext: FhirContext,
    @Value("\${app.validation.package}") val profilePath: String?
) {
    /*
        For more details on the open api support, see
        https://hapifhir.io/hapi-fhir/docs/server_plain/openapi.html

        Note that this currently ONLY works with the plain server, not the JAX-RS server.
     */
    @Bean
    @ConditionalOnProperty(prefix = "app", value = ["openapi"], havingValue = "true")
    fun openApiConfig() = FhirRestfulServerCustomizer { server ->
        server.registerInterceptor(OpenApiInterceptor())
    }

    /*
        For more details on validation, see
        https://hapifhir.io/hapi-fhir/docs/validation/instance_validator.html
        https://hapifhir.io/hapi-fhir/docs/validation/validation_support_modules.html
        https://hapifhir.io/hapi-fhir/docs/validation/examples.html
     */
    @Bean
    @ConditionalOnProperty(prefix = "app.validation", value = ["enabled"], havingValue = "true")
    fun validation() = FhirRestfulServerCustomizer { server ->
        val npmPackageSupport = NpmPackageValidationSupport(fhirContext).apply {
            loadPackageFromClasspath(profilePath)
        }

        val requestInterceptor = ValidationSupportChain(
            npmPackageSupport,
            DefaultProfileValidationSupport(fhirContext),
            CommonCodeSystemsTerminologyService(fhirContext),
            InMemoryTerminologyServerValidationSupport(fhirContext)
        ).let {
            CachingValidationSupport(it)
        }.let {
            RequestValidatingInterceptor().apply {
                addValidatorModule(FhirInstanceValidator(it))
            }
        }

        requestInterceptor.setFailOnSeverity(ResultSeverityEnum.ERROR)
        requestInterceptor.setAddResponseHeaderOnSeverity(ResultSeverityEnum.INFORMATION)
        requestInterceptor.setResponseHeaderValue("Validation on \${line}: \${message} \${severity}")
        requestInterceptor.setResponseHeaderValueNoIssues("No issues detected")

        // Now register the validating interceptor
        server.registerInterceptor(requestInterceptor)
    }
}