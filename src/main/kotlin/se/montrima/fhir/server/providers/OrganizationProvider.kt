package se.montrima.fhir.server.providers

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.rest.annotation.*
import ca.uhn.fhir.rest.api.MethodOutcome
import ca.uhn.fhir.rest.param.StringParam
import ca.uhn.fhir.rest.server.IResourceProvider
import ca.uhn.fhir.spring.boot.autoconfigure.FhirProperties
import com.mongodb.BasicDBObject
import org.bson.Document
import org.hl7.fhir.r4.model.DomainResource
import org.hl7.fhir.r4.model.IdType
import org.hl7.fhir.r4.model.Organization
import org.hl7.fhir.r4.model.StringType
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Component
import java.util.*

/*
    For general information about REST-operations see
    https://www.hl7.org/fhir/http.html
    https://www.hl7.org/fhir/search.html
*/

@Component
class OrganizationProvider(
    val fhirContext: FhirContext,
    val mongoTemplate: MongoTemplate,
    val fhirProperties: FhirProperties
): IResourceProvider {
    /*
        https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations.html#instance_read
     */

    @Read
    fun get(@IdParam id: IdType): Organization? {
        val parser = fhirContext.newJsonParser()
            .setSummaryMode(false)
            .setPrettyPrint(true)
        val query = BasicDBObject().apply {
            put("id", id.idPart)
        }
        return mongoTemplate.getCollection(Organization().fhirType())
            .find(query)
            .map {
                parser.parseResource(Organization::class.java, it.toJson())
            }.first()
    }

    /*
        https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations_search.html#search-with-no-parameters
     */
    @Search(type = Organization::class)
    fun list(): List<Organization> {
        val parser = fhirContext.newJsonParser()
            .setServerBaseUrl(fhirProperties.server.url)
                .setSummaryMode(true)
                .setPrettyPrint(true)

        return mongoTemplate.getCollection(Organization().fhirType())
            .find()
            .map {
                parser.parseResource(Organization::class.java, it.toJson())
            }.toList()
    }

    /*
        https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations_search.html#named-queries-query
     */
    @Search(type = Organization::class, queryName = "postalcode")
    fun byPostalCode(@RequiredParam(name=Organization.SP_ADDRESS_POSTALCODE) postalCode: StringParam): List<Organization> {
        val parser = fhirContext.newJsonParser()
            .setServerBaseUrl(fhirProperties.server.url)
            .setSummaryMode(true)
            .setPrettyPrint(true)

        val query = BasicDBObject().apply {
            put("address.postalCode", postalCode.value)
        }

        return mongoTemplate.getCollection(Organization().fhirType())
            .find(query)
            .map {
                parser.parseResource(Organization::class.java, it.toJson())
            }.toList()
    }

    /*
        https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations_search.html#search-parameters-string-introduction

     */
    @Search(type = Organization::class)
    fun byPostalCodeToken(@RequiredParam(name=Organization.SP_ADDRESS_POSTALCODE) postalCode: StringParam): List<Organization> {
        val parser = fhirContext.newJsonParser()
            .setServerBaseUrl(fhirProperties.server.url)
            .setSummaryMode(true)
            .setPrettyPrint(true)

        val query = BasicDBObject().apply {
            put("address.postalCode", postalCode.value)
        }

        return mongoTemplate.getCollection(Organization().fhirType())
            .find(query)
            .map {
                parser.parseResource(Organization::class.java, it.toJson())
            }.toList()
    }

    /*
        https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations.html#type_create
     */
    @Create
    fun create(@ResourceParam organization: Organization): MethodOutcome {
        if (organization.id == null) {
            organization.setId(UUID.randomUUID().toString())
        }
        Document.parse(fhirContext.newJsonParser().encodeResourceToString(organization)).let {
            mongoTemplate.insert(it, organization.fhirType())
        }
        return MethodOutcome(organization.idElement, true)
    }

    fun addOrReplaceExtension(
        extensionUrl: String,
        value: String,
        target: DomainResource
    ) : Unit {
        target.apply {
            if(hasExtension(extensionUrl)) {
                getExtensionByUrl(extensionUrl).setValue(StringType(value))
            } else {
                addExtension(
                    extensionUrl,
                    StringType(value)
                )
            }
        }
    }

    override fun getResourceType() = Organization::class.java
}