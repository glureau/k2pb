buildscript {
    val kotlinVersion: String by project
    repositories {
        mavenCentral()
        maven(url = "https://raw.githubusercontent.com/glureau/K2PB/mvn-repo")
        google()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    }
}

plugins {
    id("org.jlleitschuh.gradle.ktlint") version "14.0.1"
    kotlin("multiplatform") apply false // required for vanniktech maven publish plugin setup
    id("com.android.library") apply false
    id("com.glureau.grip") version "0.4.5"
    id("com.vanniktech.maven.publish") apply false
    id("org.jetbrains.kotlinx.kover") version "0.9.8"
}

allprojects {
    group = "com.glureau.k2pb"
    version = "0.9.29"

    repositories {
        mavenLocal()
        mavenCentral()
        google()
    }
}

dependencies {
    kover(project(":k2pb-compiler"))
    kover(project(":k2pb-runtime"))
    kover(project(":k2pb-gradle-plugin"))
    kover(project(":k2pb-serializers-datetime"))
}

kover {
    reports {
        filters {
            excludes {
                packages("com.glureau.k2pb_sample")
            }
        }
    }
}
