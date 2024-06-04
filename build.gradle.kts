plugins {
    kotlin("jvm")
    id("com.ncorti.ktfmt.gradle") version "0.18.0" apply false
}

subprojects {
    group = "tech.insight"
    version = "0.0.1-SNAPSHOT"
    
    repositories {
        mavenLocal()
        mavenCentral()
    }

    tasks.withType<Test>().configureEach { useJUnitPlatform() }
}



