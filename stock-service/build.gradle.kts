import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.1.1"
	id("io.spring.dependency-management") version "1.1.0"
	kotlin("jvm") version "1.8.22"
	kotlin("plugin.spring") version "1.8.22"
}

group = "com.mlesniak"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
	mavenCentral()
}

dependencies {
	// Kotlin
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")

	// REST
	implementation("org.springframework.boot:spring-boot-starter-web")

	// Database
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
	implementation("org.springframework.data:spring-data-mongodb:4.1.2")

	// Idiomatic logging in Kotlin.
	implementation("io.github.oshai:kotlin-logging-jvm:5.0.0")

	// OpenAPI support.
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.1.0")

	// JSON logging
	implementation("net.logstash.logback:logstash-logback-encoder:7.4")

	// General testing
	testImplementation("org.springframework.boot:spring-boot-starter-test")

	// Mocking
	implementation("com.ninja-squad:springmockk:3.0.1")

	// Testcontainers general and MongoDB support.
	testImplementation("org.springframework.boot:spring-boot-testcontainers")
	testImplementation("org.testcontainers:junit-jupiter")
	testImplementation("org.testcontainers:mongodb")

	developmentOnly("org.springframework.boot:spring-boot-devtools")

	// Enable to run docker-compose from gradle or on application startup.
	// The application expects a `compose.yml` file in the current working
	// directory.
	// Disabled for local development with both services where we want to have
	// a shared mongodb for the sake of simplicity and convenience.
	// developmentOnly("org.springframework.boot:spring-boot-docker-compose")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs += "-Xjsr305=strict"
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
