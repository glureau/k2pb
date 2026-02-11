plugins {
    kotlin("jvm")
    id("maven-publish")
    kotlin("plugin.serialization")
}

repositories {
    mavenCentral()
}

val kotlinVersion: String by project
val kspVersion: String by project

dependencies {

    implementation(project(":k2pb-annotations"))
    implementation(project(":k2pb-runtime"))
    implementation("com.squareup:kotlinpoet:2.2.0") {
        exclude(module = "kotlin-reflect")
    }
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
    implementation("com.squareup:kotlinpoet-ksp:2.2.0")
    implementation("com.google.devtools.ksp:symbol-processing:$kspVersion")
    implementation("com.google.devtools.ksp:symbol-processing-api:$kspVersion")

    testImplementation("com.github.tschuchortdev:kotlin-compile-testing-ksp:1.6.0")
    testImplementation("junit:junit:4.13.2")
    //testImplementation(kotlin("test"))
    testImplementation("org.junit.platform:junit-platform-runner:1.14.2")
    testImplementation("org.junit.jupiter:junit-jupiter:6.0.2")
    testImplementation("com.approvaltests:approvaltests:26.7.1")
}

kotlin.sourceSets.main {
    languageSettings {
        optIn("kotlin.uuid.ExperimentalUuidApi")
    }
}

sourceSets.main {
    java.srcDirs("src/main/kotlin")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    withSourcesJar()
}

setupPublishing()
