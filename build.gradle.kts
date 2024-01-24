plugins {
    kotlin("jvm")
}

allprojects {
    group = "com.hide"
    version = "0.0.1-SNAPSHOT"
    repositories {
        mavenLocal()
        mavenCentral()
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
    }
}
