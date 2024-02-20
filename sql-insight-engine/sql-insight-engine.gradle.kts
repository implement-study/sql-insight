plugins {
    kotlin("jvm")
}

subprojects {
    repositories {
        mavenLocal()
        mavenCentral()
    }
}



tasks.test {
    useJUnitPlatform()
}
