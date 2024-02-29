package se.montrima.fhir.server.dto

// A dummy model class for the purpose of this example
data class Person(
    val id: String? = null,
    val firstName: String = "",
    val lastName: String = "",
    val age: Int? = null,
    val address: Address? = null
)

data class Address(
    val street: String,
    val city: String,
    val postalCode: String
)
