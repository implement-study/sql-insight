plugins {
    kotlin("jvm")
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



