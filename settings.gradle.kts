rootProject.name = "sql-insight"

pluginManagement {
    plugins {
        kotlin("jvm") version "2.0.20"
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}


include(":sql-insight-core")
include(":sql-insight-buffer")
include(":sql-insight-engine")

include(":sql-insight-engine:sql-insight-engine-innodb")

include(":share")
include(":share:share-data")
//include(":sql-insight-server")
//include(":sql-insight-spring")


//include(":sql-insight-engine:sql-insight-engine-innodb")

require(JavaVersion.current() >= JavaVersion.VERSION_17) {
    "You must use at least Java 17 to build the project, you're currently using ${System.getProperty("java.version")}"
}

rootProject.children.forEach { it.configureBuildScriptName() }

fun ProjectDescriptor.configureBuildScriptName() {
    buildFileName = "${name}.gradle.kts"
    children.forEach { it.configureBuildScriptName() }
}
