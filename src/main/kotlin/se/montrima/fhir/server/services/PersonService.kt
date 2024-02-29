package se.montrima.fhir.server.services

import org.springframework.stereotype.Service
import se.montrima.fhir.server.dto.Person
import se.montrima.fhir.server.dto.Address

interface PersonService {
    fun getPerson(): Person
}

@Service
class PersonServiceImpl: PersonService {
    companion object {
        var counter = 0
    }
    override fun getPerson(): Person {
        counter += 1
        return Person(
            id = "$counter",
            firstName = if ( counter %2 == 0 ) "John" else "Jane",
            lastName = "Doe",
            age = 18 + counter,
            address = Address (
                street = "$counter Main St",
                city = "Springfield",
                postalCode = "123$counter"
            )
        )

    }
}
