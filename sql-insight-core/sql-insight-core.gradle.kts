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
    implementation("com.alibaba:druid:1.2.21")
    implementation("ch.qos.logback:logback-classic:1.4.14")
    implementation("com.google.guava:guava:32.1.3-jre")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.16.0")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.16.0")
    implementation("io.github.gongxuanzhang:easyByte-core:0.0.1")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
