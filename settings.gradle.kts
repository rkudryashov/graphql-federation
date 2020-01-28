rootProject.name = "graphql-federation"

include(
    "auth-service",
    "planet-service",
    "satellite-service"
)

pluginManagement {
    plugins {
        fun String.getVersion() = extra["$this.version"].toString()
        fun PluginDependenciesSpec.resolve(id: String, versionKey: String = id) = id(id) version versionKey.getVersion()

        resolve("org.jetbrains.kotlin.jvm")
        resolve("org.jetbrains.kotlin.kapt", "org.jetbrains.kotlin.jvm")
        resolve("org.jetbrains.kotlin.plugin.jpa", "org.jetbrains.kotlin.jvm")
        resolve("com.github.johnrengelman.shadow")
    }
}
