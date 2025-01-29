import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.jetbrains.kotlin.compose)
    alias(libs.plugins.jetbrains.kotlin.serialization)
    alias(libs.plugins.apollo)
    alias(libs.plugins.google.devtools.ksp)
    alias(libs.plugins.google.dagger.hilt.android)
    alias(libs.plugins.androidx.baselineprofile)
}

val appPackageName = "com.axiel7.anihyou"
val versionProps = Properties().also {
    it.load(project.rootProject.file("version.properties").reader())
}

android {
    namespace = appPackageName
    compileSdk = 35

    defaultConfig {
        applicationId = appPackageName
        minSdk = 23
        targetSdk = 35
        versionCode = versionProps.getProperty("code").toInt()
        versionName = versionProps.getProperty("name")
        setProperty("archivesBaseName", "anihyou-$versionName")

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
            "fr-rFR"
        )
    }

    buildTypes {
        val clientId = "8527"
        val malClientId = "\"9d64c3963e0f5de53083571d45016565\""

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
            buildConfigField("int", "CLIENT_ID", clientId)
            buildConfigField("String", "MAL_CLIENT_ID", malClientId)
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
            buildConfigField("int", "CLIENT_ID", clientId)
            buildConfigField("String", "MAL_CLIENT_ID", malClientId)
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

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity.compose)

    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.savedstate)

    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.work.runtime)
    implementation(libs.androidx.core.splashscreen)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)

    implementation(libs.androidx.glance.appwidget)
    implementation(libs.androidx.glance.preview)
    implementation(libs.androidx.glance.appwidget.preview)

    implementation(libs.androidx.material3)
    implementation(libs.androidx.material3.window.sizeclass)

    implementation(libs.androidx.navigation.compose)

    implementation(libs.accompanist.permissions)

    implementation(libs.placeholder.material3)
    implementation(libs.material.kolor)

    implementation(libs.coil.compose)
    implementation(libs.coil.gif)
    implementation(libs.coil.network.okhttp)

    implementation(libs.apollo.runtime)
    implementation(libs.apollo.normalized.cache)

    implementation(libs.compose.markdown)

    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    ksp(libs.androidx.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.hilt.work)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)

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

apollo {
    generateSourcesDuringGradleSync.set(false)
    service("service") {
        packageName.set(appPackageName)
        generateFragmentImplementations.set(true)
        mapScalarToKotlinInt("FuzzyDateInt")
        mapScalar(
            "CountryCode",
            "com.axiel7.anihyou.data.model.media.CountryOfOrigin",
            "com.axiel7.anihyou.data.model.media.CountryOfOrigin.countryOfOriginAdapter",
        )
        introspection {
            endpointUrl.set("https://graphql.anilist.co")
            schemaFile.set(file("src/main/graphql/schema.graphqls"))
        }
    }
}