plugins {
    kotlin("jvm")
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation(project(":sql-insight-core"))
    testImplementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.16.0")
    testImplementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.16.0")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
