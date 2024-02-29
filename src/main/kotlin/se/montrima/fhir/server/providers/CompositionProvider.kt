package se.montrima.fhir.server.providers

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.rest.annotation.*
import ca.uhn.fhir.rest.api.MethodOutcome
import ca.uhn.fhir.rest.param.StringParam
import ca.uhn.fhir.rest.server.IResourceProvider
import ca.uhn.fhir.spring.boot.autoconfigure.FhirProperties
import com.mongodb.BasicDBObject
import org.bson.Document
import org.hl7.fhir.r4.model.Address
import org.hl7.fhir.r4.model.DomainResource
import org.hl7.fhir.r4.model.HumanName
import org.hl7.fhir.r4.model.IdType
import org.hl7.fhir.r4.model.Composition
import org.hl7.fhir.r4.model.StringType
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Component
import se.montrima.fhir.server.services.PersonService
import java.util.*

/*
    For general information about REST-operations see
    https://www.hl7.org/fhir/http.html
    https://www.hl7.org/fhir/search.html
*/

@Component
class CompositionProvider(
    val fhirContext: FhirContext,
    val mongoTemplate: MongoTemplate,
    val fhirProperties: FhirProperties
): IResourceProvider {
    /*
        https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations.html#instance_read
     */

    @Read
    fun get(@IdParam id: IdType): Composition? {
        return Composition().setTitle("My Composition")
    }


    override fun getResourceType() = Composition::class.java
}