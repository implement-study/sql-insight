plugins {
    kotlin("jvm")
    importLogback
}

subprojects {
    repositories {
        mavenCentral()
        maven { setUrl("https://oss.sonatype.org/content/repositories/snapshots") }
    }
}


dependencies {
    api("com.alibaba:druid:1.2.21")
    api("com.google.guava:guava:32.1.3-jre")
    api("com.fasterxml.jackson.module:jackson-module-kotlin:2.16.0")
    api("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.16.0")
    api(project(":sql-insight-buffer"))
    testImplementation(project(":share:share-data"))
    testImplementation(kotlin("test"))
}


tasks.test {
    useJUnitPlatform()
}
