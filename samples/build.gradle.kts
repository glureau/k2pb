plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("org.ajoberstar.git-publish")
    id("org.ajoberstar.grgit")
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
        all {
            languageSettings.optIn("kotlin.RequiresOptIn")
        }
        commonMain {
            dependencies {
                //implementation(project(":lib"))
                implementation(project(":sample-lib"))
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:1.5.0")
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
            java.sourceSets {
                getByName("test").java.srcDirs("build/generated/ksp/metadata/jvmTest/java")
            }
            kotlin.srcDir("build/generated/ksp/metadata/jvmTest/kotlin")
        }
    }
}

dependencies {
    add("kspCommonMainMetadata", project(":compiler"))
}

task("runProtoc", type = Exec::class) {
    val dirPath = "build/generated/ksp/metadata/commonMain/resources/k2pb/"
    // The official gradle plugin doesn't support KMP yet: https://github.com/google/protobuf-gradle-plugin/issues/497
    // So we are assuming protoc is locally installed for now.
    // protoc: Need to generate kotlin + JAVA (kotlin is only wrapping around java, not great for KMP...)
    // onlyIf { protoFiles.isNotEmpty() } // Not possible, as proto files are also generated...
    File("$buildDir/generated/ksp/metadata/jvmTest/kotlin").mkdirs()
    File("$buildDir/generated/ksp/metadata/jvmTest/java").mkdirs()

    doFirst {
        val protoFiles = fileTree(dirPath) {
            include("**/*.proto")
        }.files
        commandLine(
            "protoc",
            "--proto_path=$dirPath",
            "--kotlin_out=build/generated/ksp/metadata/jvmTest/kotlin",
            "--java_out=build/generated/ksp/metadata/jvmTest/java",
            *protoFiles.map { it.absolutePath.substringAfter(dirPath) }.toTypedArray()
        )
    }
    dependsOn("compileCommonMainKotlinMetadata")
}

tasks["compileTestKotlinJvm"].dependsOn("runProtoc")
tasks["compileTestJava"].dependsOn("runProtoc")
