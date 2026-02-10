plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    google()
}

dependencies {
    implementation(gradleApi())
    // org.jetbrains.kotlin.multiplatform
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.2.21")
    implementation("com.android.tools.build:gradle:8.5.0")
    implementation("com.vanniktech.maven.publish:com.vanniktech.maven.publish.gradle.plugin:0.34.0")
    // add android components to check AGP version
}
