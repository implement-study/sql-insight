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
    api("com.alibaba:druid:1.2.21")
    api("ch.qos.logback:logback-classic:1.4.14")
    api("com.google.guava:guava:32.1.3-jre")
    api("com.fasterxml.jackson.module:jackson-module-kotlin:2.16.0")
    api("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.16.0")
    api("io.github.gongxuanzhang:easyByte-core:0.0.1")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
