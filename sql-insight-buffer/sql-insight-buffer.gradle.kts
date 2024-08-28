plugins {
    kotlin("jvm")
    importLogback
}


dependencies {
    implementation("io.netty:netty-buffer:4.1.112.Final")
    testImplementation(kotlin("test"))
}


tasks.test {
    useJUnitPlatform()
}
