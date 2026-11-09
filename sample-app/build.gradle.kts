plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("com.google.devtools.ksp")
    id("com.glureau.k2pb") version "0.9.37"
}

repositories {
    mavenCentral()
}
val customPackage = "com.glureau.k2pb_sample"

k2pb {
    protoPackageName = customPackage
    javaOuterClassnameSuffix = "Proto"
}

kotlin {
    jvm()

    sourceSets {
        all {
            languageSettings.optIn("kotlin.RequiresOptIn")
            languageSettings.optIn("kotlin.uuid.ExperimentalUuidApi")
            languageSettings.optIn("kotlin.time.ExperimentalTime")
        }
        commonMain {
            dependencies {
                implementation(project.dependencies.project(":k2pb-annotations"))
                implementation(project.dependencies.project(":k2pb-runtime"))
                implementation(project.dependencies.project(":k2pb-serializers-datetime"))
                implementation(project.dependencies.project(":sample-lib"))
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.8.0")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.11.0")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("org.junit.platform:junit-platform-runner:1.14.4")
                implementation("org.junit.jupiter:junit-jupiter:6.1.3")
                implementation("com.approvaltests:approvaltests:31.0.0")
                implementation("com.google.protobuf:protobuf-kotlin:4.36.2")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.11.0")
            }
            // java.sourceSets {
                // getByName("test").java.srcDirs("build/generated/ksp/jvm/jvmTest/java")
            // }
            kotlin.srcDir("build/generated/ksp/jvm/jvmTest/kotlin")
        }
    }
}

dependencies {
    add("kspJvm", project(":k2pb-compiler"))
}

task("copyProtoFiles", type = Copy::class) {
    from(
        rootDir.absolutePath + "/sample-lib/build/generated/ksp/jvm/jvmMain/resources/k2pb/" + customPackage.replace(
            ".",
            "/"
        )
    )
    into("build/generated/ksp/jvm/jvmMain/resources/k2pb/" + customPackage.replace(".", "/"))
    dependsOn(":sample-lib:kspKotlinJvm") // files should be generated before copying
    dependsOn(":sample-app:jvmProcessResources") // And are required by gradle at this point?
}

task("runProtoc", type = Exec::class) {
    dependsOn("copyProtoFiles")
    val dirPath = "build/generated/ksp/jvm/jvmMain/resources/k2pb/"
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
    dependsOn("compileKotlinJvm")
}

tasks["compileTestKotlinJvm"].dependsOn("runProtoc")
