plugins {
    kotlin("jvm") version "2.0.20"
    `java-gradle-plugin`
}


repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("net.bytebuddy:byte-buddy:1.15.0")
}


gradlePlugin {
    plugins {
        register("importLogback") {
            id = "importLogback"
            implementationClass = "tech.insight.plugin.LogbackImportPlugin"
        }
    }
}


