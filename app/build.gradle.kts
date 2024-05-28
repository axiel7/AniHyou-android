plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.apollographql.apollo3") version "4.0.0-beta.6"
    id("com.google.devtools.ksp")
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
        versionCode = 54
        versionName = "1.2.5-1"
        setProperty("archivesBaseName", "anihyou-$versionName")

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
                "az-rAZ",
                "de-rDE"
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
            isShrinkResources = false
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
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

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.activity:activity-compose:1.9.0")

    val lifecycleVersion = "2.8.0"
    implementation("androidx.lifecycle:lifecycle-runtime-compose:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:$lifecycleVersion")

    implementation("androidx.datastore:datastore-preferences:1.1.1")
    implementation("androidx.work:work-runtime:2.9.0")
    implementation("androidx.core:core-splashscreen:1.0.1")

    val composeBomVersion = "2024.05.00"
    implementation(platform("androidx.compose:compose-bom:$composeBomVersion"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")

    val materialVersion = "1.3.0-beta01"
    implementation("androidx.compose.material3:material3:$materialVersion")
    implementation("androidx.compose.material3:material3-window-size-class:$materialVersion")

    implementation("androidx.navigation:navigation-compose:2.8.0-beta01")
    implementation("androidx.glance:glance-appwidget:1.0.0")

    implementation("com.google.accompanist:accompanist-permissions:0.34.0")

    implementation("io.github.fornewid:placeholder-material3:1.1.2")
    implementation("com.materialkolor:material-kolor:1.6.1")

    val coilVersion = "3.0.0-alpha06"
    implementation("io.coil-kt.coil3:coil-compose:$coilVersion")
    implementation("io.coil-kt.coil3:coil-gif:$coilVersion")
    implementation("io.coil-kt.coil3:coil-network-okhttp:$coilVersion")

    val apolloVersion = "4.0.0-beta.6"
    implementation("com.apollographql.apollo3:apollo-runtime:$apolloVersion")
    implementation("com.apollographql.apollo3:apollo-normalized-cache:$apolloVersion")

    implementation("com.github.axiel7:compose-markdown:92ce641022")

    val hiltVersion = "2.51.1"
    implementation("com.google.dagger:hilt-android:$hiltVersion")
    ksp("com.google.dagger:hilt-android-compiler:$hiltVersion")
    val androidHiltVersion = "1.2.0"
    ksp("androidx.hilt:hilt-compiler:$androidHiltVersion")
    implementation("androidx.hilt:hilt-navigation-compose:$androidHiltVersion")
    implementation("androidx.hilt:hilt-work:$androidHiltVersion")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:$composeBomVersion"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")
}

apollo {
    generateSourcesDuringGradleSync.set(false)
    service("service") {
        packageName.set("com.axiel7.anihyou")
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