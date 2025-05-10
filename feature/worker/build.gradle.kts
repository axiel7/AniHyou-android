plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
}

val appPackageName: String by rootProject.extra
val sdkVersion: Int by rootProject.extra
val minSdkVersion: Int by rootProject.extra

android {
    namespace = "$appPackageName.feature.worker"
    compileSdk = sdkVersion

    defaultConfig {
        minSdk = minSdkVersion
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
    }
}

dependencies {
    implementation(project(":core:network"))
    implementation(project(":core:domain"))
    implementation(project(":core:ui"))

    implementation(libs.androidx.work.runtime)

    implementation(platform(libs.koin.bom))
    implementation(libs.koin.workmanager)
}
