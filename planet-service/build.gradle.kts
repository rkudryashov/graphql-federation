import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kotlinVersion: String by project
val micronautVersion: String by project
val micronautGraphQLVersion: String by project
val graphqlJavaFederationVersion: String by project
val okHttpVersion: String by project
val logbackVersion: String by project
val junitVersion: String by project
val hamcrestVersion: String by project

plugins {
    application
    id("com.github.johnrengelman.shadow")
    kotlin("jvm")
    kotlin("kapt")
    kotlin("plugin.jpa")
}

application {
    mainClassName = "io.graphqlfederation.planetservice.PlanetServiceApplication"
}

dependencies {
    implementation(platform("io.micronaut:micronaut-bom:$micronautVersion"))
    kapt(platform("io.micronaut:micronaut-bom:$micronautVersion"))

    kapt("io.micronaut:micronaut-inject-java")
    kapt("io.micronaut:micronaut-validation")
    kapt("io.micronaut.data:micronaut-data-processor")

    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    implementation("io.micronaut:micronaut-http-server-netty")
    implementation("io.micronaut.graphql:micronaut-graphql:$micronautGraphQLVersion")
    implementation("io.micronaut.data:micronaut-data-hibernate-jpa")
    implementation("io.gqljf:graphql-java-federation:$graphqlJavaFederationVersion")
    implementation("com.squareup.okhttp3:okhttp:$okHttpVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    runtimeOnly("io.micronaut.configuration:micronaut-jdbc-hikari")
    runtimeOnly("com.h2database:h2")
    runtimeOnly("ch.qos.logback:logback-classic:$logbackVersion")

    kaptTest("io.micronaut:micronaut-inject-java")
    testImplementation("io.micronaut.test:micronaut-test-junit5")
    testImplementation("org.junit.jupiter:junit-jupiter:$junitVersion")
    testImplementation("org.hamcrest:hamcrest:$hamcrestVersion")
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "11"
            freeCompilerArgs = listOf("-Xjsr305=strict")
        }
    }
    withType<Test> {
        useJUnitPlatform()
    }
    withType<ShadowJar> {
        mergeServiceFiles()
    }
}
