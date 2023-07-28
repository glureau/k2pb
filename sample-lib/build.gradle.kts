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
    jvm {
        withJava()
    }

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
                implementation("com.google.protobuf:protobuf-kotlin:3.23.0")
            }
        }
    }
}

dependencies {
    add("kspCommonMainMetadata", project(":compiler"))
}

task("runProtoc", type = Exec::class) {
    // The official gradle plugin doesn't support KMP yet: https://github.com/google/protobuf-gradle-plugin/issues/497
    // So we are assuming protoc is locally installed for now.
    // protoc: Need to generate kotlin + JAVA (kotlin is only wrapping around java, not great for KMP...)
    commandLine("protoc",
        "--proto_path=build/generated/ksp/metadata/commonMain/resources/k2pb",
        "--kotlin_out=src/jvmTest/kotlin",
        "--java_out=src/jvmTest/java",
        "build/generated/ksp/metadata/commonMain/resources/k2pb/DataClassFromLib.proto")
    dependsOn("compileCommonMainKotlinMetadata")
}

tasks["jvmTest"].dependsOn("compileCommonMainKotlinMetadata")
tasks["compileJava"].dependsOn("runProtoc")
