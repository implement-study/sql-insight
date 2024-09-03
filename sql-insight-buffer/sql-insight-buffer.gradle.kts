plugins {
    kotlin("jvm")
    importLogback
}


dependencies {
    api("io.netty:netty-buffer:4.1.112.Final")
    testImplementation(kotlin("test"))
}


tasks.test {
    useJUnitPlatform()
}
