plugins {
    kotlin("jvm")
}

subprojects {
    group = "tech.insight"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
        maven { setUrl("https://oss.sonatype.org/content/repositories/snapshots") }
    }

    tasks.withType<Test>().configureEach { useJUnitPlatform() }
}



