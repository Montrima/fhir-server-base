import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.7.0"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	kotlin("jvm") version "1.6.21"
	kotlin("plugin.spring") version "1.6.21"
}

group = "se.montrima.fhir"
version = "0.0.1-SNAPSHOT"
// val hapi_version = "5.7.4"
val hapi_version = "6.0.1"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-jersey")

	implementation("ca.uhn.hapi.fhir:hapi-fhir-structures-r4:$hapi_version")

	implementation("ca.uhn.hapi.fhir:hapi-fhir-spring-boot-starter:$hapi_version")
	implementation("ca.uhn.hapi.fhir:hapi-fhir-server:$hapi_version")

	implementation("ca.uhn.hapi.fhir:hapi-fhir-server-openapi:$hapi_version")
	implementation("ca.uhn.hapi.fhir:hapi-fhir-client-okhttp:$hapi_version")

	implementation("ca.uhn.hapi.fhir:hapi-fhir-validation:$hapi_version")
	implementation("ca.uhn.hapi.fhir:hapi-fhir-validation-resources-r4:$hapi_version")

	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	testImplementation("org.springframework.boot:spring-boot-starter-test")

//	implementation("de.flapdoodle.embed:de.flapdoodle.embed.mongo:3.4.6")
//	implementation("cz.jirutka.spring:embedmongo-spring:1.3.1")
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb")

	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "17"
	}
	inputs.files(getTasksByName("processResources", true))
}

tasks.withType<Test> {
	useJUnitPlatform()
}
