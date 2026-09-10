plugins {
    alias(libs.plugins.anihyou.feature)
}

val appPackageName = rootProject.extra["appPackageName"] as String

android {
    namespace = "$appPackageName.feature.profile"
}

dependencies {
    implementation(libs.reorderable)
    implementation(libs.androidx.navigation3.runtime)
}