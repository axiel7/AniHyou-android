plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.serialization)
    alias(libs.plugins.koin.compiler)
}

val appPackageName: String by rootProject.extra

android {
    namespace = "$appPackageName.core.domain"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
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
    implementation(project(":core:common"))
    implementation(project(":core:network"))
    implementation(project(":core:model"))
    implementation(project(":core:resources"))

    implementation(libs.apollo.normalized.cache)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.datastore.preferences)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui.graphics)

    implementation(platform(libs.koin.bom))
    implementation(libs.koin.compose)

    coreLibraryDesugaring(libs.desugar.jdk.libs)
}