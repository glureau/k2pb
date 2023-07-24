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
    jvm()

    sourceSets {
        all {
            languageSettings.optIn("kotlin.RequiresOptIn")
        }
        commonMain {
            dependencies {
                //implementation(project(":lib"))
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
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

// Publish the sample documentation on branch "demo"
gitPublish {
    repoUri.set("git@github.com:glureau/K2PB.git")
    branch.set("demo")
    contents.from("$buildDir/dokka/")
    preserve { include("**") }
    val head = grgit.head()
    commitMessage.set("${head.abbreviatedId}: ${project.version} : ${head.fullMessage}")
}

tasks["jvmTest"].dependsOn("compileCommonMainKotlinMetadata")
