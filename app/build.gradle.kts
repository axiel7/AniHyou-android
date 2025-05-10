import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.jetbrains.kotlin.compose)
    alias(libs.plugins.androidx.baselineprofile)
}

val appPackageName: String by rootProject.extra
val sdkVersion: Int by rootProject.extra
val minSdkVersion: Int by rootProject.extra

val versionProps = Properties().also {
    it.load(project.rootProject.file("version.properties").reader())
}

android {
    namespace = appPackageName
    compileSdk = sdkVersion

    defaultConfig {
        applicationId = appPackageName
        minSdk = minSdkVersion
        targetSdk = sdkVersion
        versionCode = versionProps.getProperty("code").toInt()
        versionName = versionProps.getProperty("name")

        base {
            archivesName = "anihyou-$versionName"
        }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    androidResources {
        localeFilters += listOf(
            "en",
            "ja-rJP",
            "ru-rRU",
            "es-rES",
            "tr-rTR",
            "pt-rBR",
            "ar-rSA",
            "in-rID",
            "it-rIT",
            "uk-rUA",
            "pl-rPL",
            "az-rAZ",
            "de-rDE",
            "zh-rCN",
            "zh-rTW",
            "fr-rFR",
            "th-rTH"
        )
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-DEBUG"
            isDebuggable = true
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            resValue("string", "app_name", "AniHyou Debug")
        }
        release {
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    splits {
        abi {
            isEnable = true
            reset()
            include("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
            isUniversalApk = true
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
        aidl = false
        renderScript = false
        shaders = false
    }
    androidResources {
        aaptOptions.cruncherEnabled = false
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    dependenciesInfo {
        includeInApk = false
    }
    baselineProfile {
        dexLayoutOptimization = true
    }
}

dependencies {
    implementation(project(":core:network"))
    implementation(project(":core:domain"))
    implementation(project(":core:ui"))
    implementation(project(":feature:activitydetails"))
    implementation(project(":feature:calendar"))
    implementation(project(":feature:characterdetails"))
    implementation(project(":feature:editmedia"))
    implementation(project(":feature:explore"))
    implementation(project(":feature:home"))
    implementation(project(":feature:login"))
    implementation(project(":feature:mediadetails"))
    implementation(project(":feature:notifications"))
    implementation(project(":feature:profile"))
    implementation(project(":feature:reviewdetails"))
    implementation(project(":feature:settings"))
    implementation(project(":feature:staffdetails"))
    implementation(project(":feature:studiodetails"))
    implementation(project(":feature:thread"))
    implementation(project(":feature:usermedialist"))
    implementation(project(":feature:widget"))
    implementation(project(":feature:worker"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity.compose)

    implementation(libs.androidx.core.splashscreen)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.tooling.preview)

    implementation(libs.androidx.material3)
    implementation(libs.androidx.material3.window.sizeclass)

    implementation(libs.androidx.navigation.compose)

    implementation(libs.androidx.wear.remote.interactions)

    implementation(libs.accompanist.permissions)

    implementation(libs.coil.compose)
    implementation(libs.coil.gif)
    implementation(libs.coil.network.okhttp)

    implementation(platform(libs.koin.bom))
    implementation(libs.koin.compose)
    implementation(libs.koin.workmanager)
    implementation(libs.koin.startup)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)

    coreLibraryDesugaring(libs.desugar.jdk.libs)

    implementation(libs.androidx.profileinstaller)
    "baselineProfile"(project(":baselineprofile"))
}