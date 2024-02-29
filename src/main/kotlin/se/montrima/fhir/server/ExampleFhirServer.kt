package se.montrima.fhir.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ForDevelopersApplication

fun main(args: Array<String>) {
	runApplication<ForDevelopersApplication>(*args)
}
