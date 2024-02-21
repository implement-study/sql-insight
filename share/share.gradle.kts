plugins {
    kotlin("jvm")
}

subprojects {
    repositories {
        mavenLocal()
        mavenCentral()
    }
}


dependencies {
}


tasks.test {
    useJUnitPlatform()
}
