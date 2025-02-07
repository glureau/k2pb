rootProject.name = "k2pb"
pluginManagement {
    val kspVersion: String by settings

    plugins {
        id("com.google.devtools.ksp") version kspVersion
    }
    repositories {
        mavenLocal()
        gradlePluginPortal()
        google()
    }
}

include(":k2pb-annotations")
include(":k2pb-compiler")
include(":k2pb-gradle-plugin")
include(":k2pb-runtime")
include(":k2pb-serializers-datetime")

include(":sample-lib")
include(":sample-app")
