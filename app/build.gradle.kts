import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.apollographql.apollo3") version "4.0.0-beta.4"
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
}

android.buildFeatures.buildConfig = true

android {
    namespace = "com.axiel7.anihyou"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.axiel7.anihyou"
        minSdk = 23
        targetSdk = 34
        versionCode = 46
        versionName = "1.2.3-1"
        archivesName.set("anihyou-$versionName")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        resourceConfigurations.addAll(
            listOf(
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
            )
        )
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        val clientId = "8527"

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
        }
        release {
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("int", "CLIENT_ID", clientId)
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        aidl = false
        renderScript = false
        shaders = false
    }
    androidResources {
        aaptOptions.cruncherEnabled = false
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.7"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    dependenciesInfo {
        includeInApk = false
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.activity:activity-compose:1.8.2")

    val lifecycleVersion = "2.6.2"
    implementation("androidx.lifecycle:lifecycle-runtime-compose:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:$lifecycleVersion")

    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("androidx.work:work-runtime:2.9.0")
    implementation("androidx.core:core-splashscreen:1.0.1")

    val composeBomVersion = "2023.10.01"
    implementation(platform("androidx.compose:compose-bom:$composeBomVersion"))
    implementation("androidx.compose.ui:ui:1.6.0-rc01")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")

    val materialVersion = "1.2.0-beta01"
    implementation("androidx.compose.material3:material3:$materialVersion")
    implementation("androidx.compose.material3:material3-window-size-class:$materialVersion")

    implementation("androidx.navigation:navigation-compose:2.7.6")
    implementation("androidx.glance:glance-appwidget:1.0.0")

    implementation("com.google.accompanist:accompanist-permissions:0.32.0")

    implementation("io.github.fornewid:placeholder-material3:1.0.1")
    implementation("com.materialkolor:material-kolor:1.3.0")

    val coilVersion = "2.5.0"
    implementation("io.coil-kt:coil-compose:$coilVersion")
    implementation("io.coil-kt:coil-gif:$coilVersion")

    val apolloVersion = "4.0.0-beta.4"
    implementation("com.apollographql.apollo3:apollo-runtime:$apolloVersion")
    implementation("com.apollographql.apollo3:apollo-normalized-cache:$apolloVersion")

    implementation("com.github.axiel7:compose-markdown:5c1c0662a0")

    val hiltVersion = "2.50"
    implementation("com.google.dagger:hilt-android:$hiltVersion")
    kapt("com.google.dagger:hilt-android-compiler:$hiltVersion")
    kapt("androidx.hilt:hilt-compiler:1.1.0")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
    implementation("androidx.hilt:hilt-work:1.1.0")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:$composeBomVersion"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")
}

kapt {
    correctErrorTypes = true
}

apollo {
    generateSourcesDuringGradleSync.set(false)
    service("service") {
        packageName.set("com.axiel7.anihyou")
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