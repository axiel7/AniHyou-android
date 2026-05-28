plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.compose)
    alias(libs.plugins.jetbrains.kotlin.serialization)
}

val appPackageName: String by rootProject.extra

android {
    namespace = "$appPackageName.core.ui"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }

    buildFeatures {
        compose = true
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
    api(project(":core:base"))
    api(project(":core:common"))
    api(project(":core:resources"))
    api(project(":core:model"))
    implementation(project(":core:network"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)

    implementation(libs.kotlinx.serialization.json)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    api(libs.material.kolor)
    implementation(libs.placeholder.material3)

    api(libs.androidx.navigation3.runtime)
    api(libs.androidx.navigation3.ui)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)

    implementation(libs.coil.compose)

    implementation(libs.haze.blur)

    implementation(libs.markdown.renderer)
    implementation(libs.markdown.renderer.material3)
    implementation(libs.markdown.renderer.coil3)

    coreLibraryDesugaring(libs.desugar.jdk.libs)

    debugImplementation(libs.ui.tooling)
}
