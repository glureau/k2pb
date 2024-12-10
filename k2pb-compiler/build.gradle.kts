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
    implementation("com.squareup:kotlinpoet:1.10.2") {
        exclude(module = "kotlin-reflect")
    }
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
    implementation("com.squareup:kotlinpoet-ksp:1.16.0")
    implementation("com.google.devtools.ksp:symbol-processing:$kspVersion")
    implementation("com.google.devtools.ksp:symbol-processing-api:$kspVersion")

    testImplementation("com.github.tschuchortdev:kotlin-compile-testing-ksp:1.4.9")
    testImplementation("junit:junit:4.13.2")
    //testImplementation(kotlin("test"))
    testImplementation("org.junit.platform:junit-platform-runner:1.9.3")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    testImplementation("com.approvaltests:approvaltests:18.4.0")
}

sourceSets.main {
    java.srcDirs("src/main/kotlin")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "compiler"

            from(components["java"])
        }
    }
}
