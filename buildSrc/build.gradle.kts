plugins {
    kotlin("jvm") version "2.0.20"
    `java-gradle-plugin`
}


repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    //    implementation("com.intellij:annotations:latest.release")
}

gradlePlugin {
    plugins {
        register("importLogback") {
            id = "importLogback"
            implementationClass = "tech.insight.plugin.LogbackImportPlugin"
        }
    }
}


