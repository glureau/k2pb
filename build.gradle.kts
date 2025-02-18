buildscript {
    val kotlinVersion: String by project
    repositories {
        mavenCentral()
        maven(url = "https://raw.githubusercontent.com/glureau/K2PB/mvn-repo")
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    }
}

plugins {
    id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
    id("com.android.library") version "7.3.0" apply false
}

allprojects {
    group = "com.glureau.k2pb"
    version = "0.9.10"

    repositories {
        mavenLocal()
        mavenCentral()
        google()
    }
}
