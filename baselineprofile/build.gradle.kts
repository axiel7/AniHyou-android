plugins {
    alias(libs.plugins.android.test)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.androidx.baselineprofile)
}

val appPackageName: String by rootProject.extra
val sdkVersion: Int by rootProject.extra

android {
    namespace = "$appPackageName.baselineprofile"
    compileSdk = sdkVersion

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlin {
        compilerOptions {
            jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
        }
    }

    defaultConfig {
        minSdk = 28
        targetSdk = sdkVersion

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        missingDimensionStrategy("version", "foss")
        missingDimensionStrategy("version", "gms")
    }

    targetProjectPath = ":app"

}

// This is the configuration block for the Baseline Profile plugin.
// You can specify to run the generators on a managed devices or connected devices.
baselineProfile {
    useConnectedDevices = true
}

dependencies {
    implementation(libs.androidx.junit)
    implementation(libs.androidx.espresso.core)
    implementation(libs.androidx.uiautomator)
    implementation(libs.androidx.benchmark.macro.junit4)
}

androidComponents {
    onVariants { v ->
        val artifactsLoader = v.artifacts.getBuiltArtifactsLoader()
        v.instrumentationRunnerArguments.put(
            "targetAppId",
            v.testedApks.map { artifactsLoader.load(it)!!.applicationId }
        )
    }
}