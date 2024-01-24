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
}
