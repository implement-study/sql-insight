
plugins {
    kotlin("jvm") version "2.0.0"
    `kotlin-dsl`
    `java-library`
}


repositories {
    mavenLocal()
    mavenCentral()
}

dependencies{
}

gradlePlugin {
    plugins {
        register("importLogback") {
            id = "importLogback"
            implementationClass = "tech.insight.plugin.LogbackImportPlugin"
        }
    }
}


