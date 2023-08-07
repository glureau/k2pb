plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

val kotlinVersion: String by project
val kspVersion: String by project

dependencies {
    implementation("com.google.devtools.ksp:symbol-processing:$kspVersion")
    implementation("com.google.devtools.ksp:symbol-processing-api:$kspVersion")
    api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
}
