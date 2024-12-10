plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("maven-publish")
}

android {
    namespace = "com.glureau.k2pb.runtime"
    compileSdk = 33
    buildToolsVersion = "33.0.0"
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

    defaultConfig {
        minSdk = 21
        targetSdk = 33
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    explicitApi()

    js(IR) {
        browser()
        nodejs()
    }
    android { publishLibraryVariants("release", "debug") }
    jvm {
        val main by compilations.getting {
            compilerOptions.configure {
                jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
            }
        }
    }
    applyDefaultHierarchyTemplate()
    ios()
    iosSimulatorArm64()
    tvos()
    watchos()
    macosArm64()
    macosX64()


    sourceSets {
        all {
            languageSettings.optIn("kotlin.RequiresOptIn")
        }
        commonMain {
            dependencies {
                api(project(":k2pb-annotations"))
            }
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }

    targets.all {
        compilations.all {
            // Cannot enable rn due to native issue (stdlib included more than once)
            // may be related to https://youtrack.jetbrains.com/issue/KT-46636
            kotlinOptions.allWarningsAsErrors = false
        }
    }/*
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }*/
}
/*
// For when nodejs.org is down...
rootProject.plugins.withType<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin> {
    rootProject.the<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension>().nodeVersion = "18.13.0"
}*/