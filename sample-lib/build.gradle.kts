plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("com.google.devtools.ksp")
    //id("com.glureau.k2pb") version "0.1.0"
}

repositories {
    mavenCentral()
}

kotlin {
    jvm()

    sourceSets {
        commonMain {
            dependencies {
                //implementation(project(":lib"))
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:1.5.0")
            }
        }
        commonTest {
            dependencies {
                //implementation(kotlin("test-common"))
                //implementation(kotlin("test-annotations-common"))
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("org.junit.platform:junit-platform-runner:1.9.3")
                implementation("org.junit.jupiter:junit-jupiter:5.9.3")
                implementation("com.approvaltests:approvaltests:18.4.0")
            }
        }
    }
}

dependencies {
    add("kspCommonMainMetadata", project(":compiler"))
}

tasks["jvmTest"].dependsOn("compileCommonMainKotlinMetadata")
