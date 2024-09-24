plugins {
    kotlin("jvm")
}



dependencies {
    api(project(":sql-insight-core"))
    testImplementation(project(":share:share-data"))
    testImplementation(kotlin("test"))
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.2.1")
}



tasks.test {
    useJUnitPlatform()
}
