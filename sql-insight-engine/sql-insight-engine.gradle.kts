plugins {
    kotlin("jvm")
}

subprojects {
    repositories {
        mavenCentral()
        maven { setUrl("https://oss.sonatype.org/content/repositories/snapshots") }
    }
}



tasks.test {
    useJUnitPlatform()
}
