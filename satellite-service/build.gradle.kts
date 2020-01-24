import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

// todo remove unneeded
val kotlinVersion: String by project
val micronautVersion: String by project
val micronautDataVersion: String by project
val logbackVersion: String by project
val junitVersion: String by project
val apolloFederationJavaVersion: String by project

plugins {
    application
    id("com.github.johnrengelman.shadow")
    kotlin("jvm")
    kotlin("kapt")
    kotlin("plugin.jpa")
}

application {
    mainClassName = "example.Application"
}

dependencies {
    implementation(platform("io.micronaut:micronaut-bom:$micronautVersion"))
    kapt(platform("io.micronaut:micronaut-bom:$micronautVersion"))

    kapt("io.micronaut:micronaut-inject-java")
    kapt("io.micronaut:micronaut-validation")
    kapt("io.micronaut.data:micronaut-data-processor:$micronautDataVersion")

    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    implementation("io.micronaut:micronaut-http-server-netty")
    implementation("io.micronaut.graphql:micronaut-graphql")
    implementation("io.micronaut.data:micronaut-data-hibernate-jpa:$micronautDataVersion")
    implementation("com.apollographql.federation:federation-graphql-java-support:$apolloFederationJavaVersion")
    runtimeOnly("io.micronaut.configuration:micronaut-jdbc-hikari")
    runtimeOnly("com.h2database:h2")
    runtimeOnly("ch.qos.logback:logback-classic:$logbackVersion")

    kaptTest("io.micronaut:micronaut-inject-java")
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