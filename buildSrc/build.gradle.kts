
plugins {
    kotlin("jvm") version "1.8.20"
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


