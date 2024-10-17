rootProject.name = "k2pb"
pluginManagement {
    val kspVersion: String by settings
    val kotlinVersion: String by settings

    plugins {
        id("com.google.devtools.ksp") version kspVersion
        kotlin("plugin.serialization") version kotlinVersion
    }
    repositories {
        mavenLocal()
        gradlePluginPortal()
        google()
    }
}

include(":annotations")
include(":compiler")
include(":runtime")
include(":gradle-plugin")

include(":sample-lib")
include(":sample-app")
