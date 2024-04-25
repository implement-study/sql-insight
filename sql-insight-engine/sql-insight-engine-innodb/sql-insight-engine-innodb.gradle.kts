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

}


tasks.test {
    useJUnitPlatform()
}
