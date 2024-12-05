import javax.script.ScriptEngineManager

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

ksp {
    arg("k2pb:replacement", "BigDecimal=kotlin.String;java.math.BigDecimal=kotlin.String;AnotherCustomType=kotlin.Int")
    //ScriptEngineManager().getEngineByName("").eval("")
    val a = "com.glureau.sample.Vehicle"
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
                implementation(project(":annotations"))
                implementation(project(":k2pb-runtime"))
                implementation(project(":k2pb-serializers-datetime"))
                implementation(project(":sample-lib"))
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("org.junit.platform:junit-platform-runner:1.10.2")
                implementation("org.junit.jupiter:junit-jupiter:5.10.2")
                implementation("com.approvaltests:approvaltests:18.4.0")
                implementation("com.google.protobuf:protobuf-kotlin:4.26.0")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
            }
            java.sourceSets {
                getByName("test").java.srcDirs("build/generated/ksp/jvm/jvmTest/java")
            }
            kotlin.srcDir("build/generated/ksp/jvm/jvmTest/kotlin")
        }
    }
}

dependencies {
    add("kspJvm", project(":compiler"))
}

task("copyProtoFiles", type = Copy::class) {
    from(rootDir.absolutePath + "/sample-lib/build/generated/ksp/jvm/jvmMain/resources/k2pb/")
    into("build/generated/ksp/jvm/jvmMain/resources/k2pb/")
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
        // Copy files from sample-lib, as protoc requires every files to be present in the same directory
        File("$rootDir/sample-lib/build/generated/ksp/jvm/jvmMain/resources/k2pb/")
            .listFiles().forEach {
                it.copyTo(File("$buildDir/generated/ksp/jvm/jvmMain/resources/k2pb/${it.name}"), true)
            }
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
        println("Running protoc: $cmd")
        commandLine(cmd)
    }
    dependsOn("compileKotlinJvm")
}

tasks["compileTestKotlinJvm"].dependsOn("runProtoc")
