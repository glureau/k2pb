plugins {
    kotlin("jvm")
    id("maven-publish")
    id("com.gradle.plugin-publish") version "1.3.1"
    `java-gradle-plugin`
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of("17"))
    }
}

repositories {
    // Use Maven Central for resolving dependencies
    mavenCentral()
}

val kspVersion: String by properties

dependencies {
    implementation(gradleApi())
    implementation("com.google.devtools.ksp:com.google.devtools.ksp.gradle.plugin:$kspVersion")
}

gradlePlugin {
    website = "https://github.com/glureau/k2pb"
    vcsUrl = "https://github.com/glureau/k2pb"
    plugins {
        create("k2pb") {
            id = "com.glureau.k2pb"
            implementationClass = "com.glureau.k2pb.gradle.K2PBGradlePlugin"
            displayName = "Easily serialize in protobuf from your kotlin code"
            description = "Generate protobuf files & serialize to protobuf from your data classes"
            tags = listOf("k2pb", "kotlin", "serialization", "protobuf")
        }
    }
}
