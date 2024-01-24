
plugins {
    kotlin("jvm")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
