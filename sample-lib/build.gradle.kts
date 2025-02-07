plugins {
    kotlin("multiplatform")
    id("com.google.devtools.ksp")
    id("com.glureau.k2pb") version "0.9.10-SNAPSHOT"
}

repositories {
    mavenCentral()
}

val customPackage = "com.glureau.k2pb_sample"

k2pb {
    protoPackageName = customPackage
    javaOuterClassnameSuffix = "Proto"
    javaPackage = "com.glureau.custom.javapackage"
}

kotlin {
    jvm {
        withJava()
    }
    iosX64()

    sourceSets {
        commonMain {
            kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
            dependencies {
                implementation(project(":k2pb-runtime"))
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("org.junit.platform:junit-platform-runner:1.10.2")
                implementation("org.junit.jupiter:junit-jupiter:5.10.2")
                implementation("com.approvaltests:approvaltests:18.4.0")
                implementation("com.google.protobuf:protobuf-kotlin:4.26.0")
            }
            java.sourceSets {
                getByName("test").java.srcDirs("build/generated/ksp/jvm/jvmTest/java")
            }
            kotlin.srcDir("build/generated/ksp/jvm/jvmTest/kotlin")
        }
    }
}

dependencies {
    add("kspCommonMainMetadata", project(":k2pb-compiler"))
}

task("runProtoc", type = Exec::class) {
    val dirPath = "build/generated/ksp/metadata/commonMain/resources/k2pb/"
    // The official gradle plugin doesn't support KMP yet: https://github.com/google/protobuf-gradle-plugin/issues/497
    // So we are assuming protoc is locally installed for now.
    // protoc: Need to generate kotlin + JAVA (kotlin is only wrapping around java, not great for KMP...)
    // onlyIf { protoFiles.isNotEmpty() } // Not possible, as proto files are also generated...

    doFirst {
        File("$buildDir/generated/ksp/jvm/jvmTest/kotlin").mkdirs()
        File("$buildDir/generated/ksp/jvm/jvmTest/java").mkdirs()

        val protoFiles = fileTree(dirPath) {
            include("**/*.proto")
        }.files
        val cmd = listOf(
            "$rootDir/protoc/bin/protoc",
            "--proto_path=$dirPath",
            "--kotlin_out=build/generated/ksp/jvm/jvmTest/kotlin",
            "--java_out=build/generated/ksp/jvm/jvmTest/java",
            *protoFiles.map { it.absolutePath.substringAfter(dirPath) }.toTypedArray()
        )
        println("Running protoc:\n------\n${cmd.joinToString(" ")}\n------")
        commandLine(cmd)
    }
    dependsOn("kspCommonMainKotlinMetadata")
}

tasks["compileKotlinJvm"].dependsOn("kspCommonMainKotlinMetadata")
tasks["compileTestKotlinJvm"].dependsOn("runProtoc")
