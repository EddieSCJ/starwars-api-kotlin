import org.jetbrains.kotlin.contracts.model.structure.UNKNOWN_COMPUTATION.type
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.spring") version "1.6.21"
    kotlin("plugin.serialization") version "1.4.10"

    id ("jacoco")
    id ("org.sonarqube") version "3.3"

    id("org.springframework.boot") version "2.7.0"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
}

group = "com.starwars"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {

    /** Kotlin **/
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.6.2")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:1.1.6")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")

    /** Spring **/
    developmentOnly("org.springframework.boot:spring-boot-devtools:2.7.0")
    implementation("org.springframework.boot:spring-boot-starter-webflux:2.7.0")
    implementation("org.springframework.boot:spring-boot-starter-actuator:2.7.0")

    /** Spring Cloud **/
    implementation("org.springframework.cloud:spring-cloud-context:3.1.3")

    /** Messaging **/
    implementation("org.springframework.kafka:spring-kafka:2.8.6")
    implementation("org.springframework.cloud:spring-cloud-starter-aws-messaging:2.2.6.RELEASE")


    /** JWT **/
    implementation("io.jsonwebtoken:jjwt:0.9.1")

    /** Reactive Feign **/
    implementation("com.playtika.reactivefeign:feign-reactor-core:3.2.1")
    implementation("com.playtika.reactivefeign:feign-reactor-spring-configuration:3.2.1")
    implementation("com.playtika.reactivefeign:feign-reactor-webclient:3.2.1")

    /** Database **/
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive:2.7.0")

    /** Docs **/
    implementation("org.springdoc:springdoc-openapi-ui:1.6.9")

    /** Testing **/
    testImplementation("com.appmattus.fixture:fixture:1.2.0")
    testImplementation("io.projectreactor:reactor-test:3.4.18")
    testImplementation ("org.junit.jupiter:junit-jupiter:5.8.2")
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.0.0")
    testImplementation ("org.springframework.boot:spring-boot-starter-test:2.7.0")
}

// ================================ Tests ================================
// Configuration to jacoco produce xml
tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.jacocoTestReport {
    reports {
        xml.isEnabled = true
        xml.destination  = File("$buildDir/reports/jacoco/report.xml")
        csv.isEnabled = false
        html.isEnabled = false
    }
    executionData(File("build/jacoco/test.exec"))
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}