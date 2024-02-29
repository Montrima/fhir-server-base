package se.montrima.fhir.server.configuration.hapi_extension

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.rest.server.HardcodedServerAddressStrategy
import ca.uhn.fhir.rest.server.IPagingProvider
import ca.uhn.fhir.rest.server.IResourceProvider
import ca.uhn.fhir.rest.server.RestfulServer
import ca.uhn.fhir.rest.server.interceptor.IServerInterceptor
import ca.uhn.fhir.spring.boot.autoconfigure.FhirProperties
import ca.uhn.fhir.spring.boot.autoconfigure.FhirRestfulServerCustomizer
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.web.servlet.ServletRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.AnnotationAwareOrderComparator

// TODO: Note that this is a "auick-fix"
/*
    This class is more or less copied from the autoconfiguration to handle a plain server
    instead of only JAX-RS server.

    The main difference is in the conditions on which this is to be injected into context plus
    in the initialize method which strips '/ *' from path due to issues with swagger-ui.
 */
@Configuration
@EnableConfigurationProperties(FhirProperties::class)
@ConfigurationProperties("hapi.fhir.rest")
internal class FhirRestfulServerConfiguration(
    val properties: FhirProperties,
    fhirContext: FhirContext,
    @Autowired(required = false) val localResourceProviders: List<IResourceProvider>?,
    @Autowired(required = false) val localPagingProvider: IPagingProvider?,
    @Autowired(required = false) val interceptors: List<IServerInterceptor>?,
    @Autowired(required = false) val customizers: List<FhirRestfulServerCustomizer>?
) : RestfulServer(fhirContext) {

    val log = LoggerFactory.getLogger(javaClass)

    private fun customize() {
        customizers?.let {
            AnnotationAwareOrderComparator.sort(it)
            it
        }?.forEach {
            it.customize(this)
        }
    }

    @Bean
    fun fhirServerRegistrationBean(): ServletRegistrationBean<*> =
        ServletRegistrationBean(this, properties.server.path).apply {
            setLoadOnStartup(1)
        }

    override fun initialize() {
        log.debug("Initilized plain server")
        super.initialize()
        setFhirContext(fhirContext)
        setResourceProviders(localResourceProviders)
        setPagingProvider(localPagingProvider)
        interceptors?.forEach {
            registerInterceptor(it)
        }
        serverAddressStrategy = HardcodedServerAddressStrategy(properties.server.path.replace("/*", ""))
        customize()
    }
}