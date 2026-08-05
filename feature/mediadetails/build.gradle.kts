plugins {
    alias(libs.plugins.anihyou.feature)
}

val appPackageName: String by rootProject.extra

android {
    namespace = "$appPackageName.feature.mediadetails"
}

dependencies {
    implementation(project(":feature:editmedia"))
}
