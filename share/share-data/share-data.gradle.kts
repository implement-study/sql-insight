plugins {
    kotlin("jvm")
}

repositories {
    mavenLocal()
    mavenCentral()
}


dependencies {
}



tasks.test {
    useJUnitPlatform()
}
