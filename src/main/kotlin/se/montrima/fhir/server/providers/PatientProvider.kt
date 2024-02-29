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
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.StringType
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Component
import se.montrima.fhir.server.services.PersonService

@Component
class PatientProvider(
    val fhirContext: FhirContext,
    val mongoTemplate: MongoTemplate,
    val fhirProperties: FhirProperties,
    val personService: PersonService
): IResourceProvider {
    @Read
    fun get(@IdParam id: IdType): Patient? {

        val person = personService.getPerson()
        return Patient().setName(
            listOf(
                HumanName().setFamily(person.lastName).addGiven(person.firstName)
            )
        ).setAddress(
            listOf(
                Address()
                    .setPostalCode(person.address?.postalCode)
                    .setCity(person.address?.city)
                    .setLine( mutableListOf( StringType(person.address?.street) ) )

            )
        )

    }

    @Search(type = Patient::class)
    fun list(): List<Patient> {
        val parser = fhirContext.newJsonParser()
            .setServerBaseUrl(fhirProperties.server.url)
                .setSummaryMode(true)
                .setPrettyPrint(true)

        return mongoTemplate.getCollection(Patient().fhirType())
            .find()
            .map {
                parser.parseResource(Patient::class.java, it.toJson())
            }.toList()
    }

    override fun getResourceType() = Patient::class.java
}