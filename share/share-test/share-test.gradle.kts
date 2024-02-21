plugins {
    kotlin("jvm")
}

repositories {
    mavenLocal()
    mavenCentral()
}


dependencies {
    implementation(project(":sql-insight-core"))
}



tasks.test {
    useJUnitPlatform()
}
