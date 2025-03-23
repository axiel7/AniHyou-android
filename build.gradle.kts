// Top-level build file where you can add configuration options common to all sub-projects/modules.
val appPackageName by extra { "com.axiel7.anihyou" }
val sdkVersion by extra { 35 }
val minSdkVersion by extra { 23 }

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.jetbrains.kotlin.compose) apply false
    alias(libs.plugins.jetbrains.kotlin.serialization) apply false
    alias(libs.plugins.google.devtools.ksp) apply false
    alias(libs.plugins.google.dagger.hilt.android) apply false
    alias(libs.plugins.android.test) apply false
    alias(libs.plugins.androidx.baselineprofile) apply false
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false
}