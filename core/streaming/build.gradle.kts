val sdkVersion: Int by rootProject.extra
val minSdkVersion: Int by rootProject.extra

plugins {
    alias(libs.plugins.android.library)
    kotlin("android")
    alias(libs.plugins.jetbrains.kotlin.serialization)
}

android {
    namespace = "com.axiel7.anihyou.core.streaming"
    compileSdk = sdkVersion
    
    defaultConfig {
        minSdk = minSdkVersion
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.coil.network.okhttp)
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)
}
