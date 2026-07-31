plugins {
    alias(libs.plugins.anihyou.feature)
}

val appPackageName: String by rootProject.extra

android {
    namespace = "$appPackageName.feature.profile"
}

dependencies {
    implementation(libs.reorderable)
    implementation(libs.androidx.navigation3.runtime)
}