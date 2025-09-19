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
    id("org.jlleitschuh.gradle.ktlint") version "13.1.0"
    kotlin("multiplatform") apply false // required for vanniktech maven publish plugin setup
    id("com.android.library") apply false
    id("com.glureau.grip") version "0.4.5"
    id("com.vanniktech.maven.publish") apply false
}

allprojects {
    group = "com.glureau.k2pb"
    version = "0.9.23"

    repositories {
        mavenLocal()
        mavenCentral()
        google()
    }
}
