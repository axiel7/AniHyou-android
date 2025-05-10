plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
}

val appPackageName: String by rootProject.extra
val sdkVersion: Int by rootProject.extra
val minSdkVersion: Int by rootProject.extra

android {
    namespace = "$appPackageName.core.common"
    compileSdk = sdkVersion

    defaultConfig {
        minSdk = minSdkVersion
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
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
    implementation(project(":core:base"))
    implementation(project(":core:resources"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.lifecycle.viewmodel)

    coreLibraryDesugaring(libs.desugar.jdk.libs)
}
