import tech.insight.plugin.jvm.DebugTimePlug

plugins {
    kotlin("jvm")
    id("net.bytebuddy.byte-buddy-gradle-plugin") version "1.15.1"
}


byteBuddy {
    transformation {
        plugin = DebugTimePlug::class.java
    }
}


dependencies {
    api(project(":sql-insight-core"))
    testImplementation(project(":share:share-data"))
    testImplementation(kotlin("test"))
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.2.1")
}



tasks.test {
    useJUnitPlatform()
}
