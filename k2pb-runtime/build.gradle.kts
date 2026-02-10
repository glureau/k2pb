plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

android {
    namespace = "com.glureau.k2pb.runtime"
    compileSdk = 36
    buildToolsVersion = "36.0.0"
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

    defaultConfig {
        minSdk = 21
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
    androidTarget {
        publishLibraryVariants("release", "debug")
    }
    jvm {
        val main by compilations.getting {
            compilerOptions.configure {
                jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
            }
        }
    }
    applyDefaultHierarchyTemplate()
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    tvosArm64()
    tvosSimulatorArm64()
    watchosArm32()
    watchosArm64()
    macosArm64()
    macosX64()

    withSourcesJar()

    sourceSets {
        all {
            languageSettings.optIn("kotlin.RequiresOptIn")
        }
        commonMain {
            dependencies {
                api(project.dependencies.project(":k2pb-annotations"))
            }
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }

    compilerOptions {
        allWarningsAsErrors.set(true)
    }
}

setupPublishing()