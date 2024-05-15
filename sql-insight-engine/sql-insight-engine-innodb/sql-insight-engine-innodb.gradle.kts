plugins {
    kotlin("jvm")
}

repositories {
    mavenLocal()
    mavenCentral()
}


dependencies {
    implementation(project(":sql-insight-core"))
    testImplementation(project(":share:share-data"))
    testImplementation(kotlin("test"))
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.0.0")
}


tasks.test {
    useJUnitPlatform()
}
