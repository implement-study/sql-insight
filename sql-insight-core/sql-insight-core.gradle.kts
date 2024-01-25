plugins {
    kotlin("jvm")
}

subprojects {
    repositories {
        mavenLocal()
        mavenCentral()
    }
}

dependencies{
    implementation("com.alibaba:druid:1.2.21")
    implementation("ch.qos.logback:logback-classic:1.4.14")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
