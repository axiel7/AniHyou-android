plugins {
    alias(libs.plugins.anihyou.feature)
}

val appPackageName = rootProject.extra["appPackageName"] as String

android {
    namespace = "$appPackageName.feature.mediadetails"
}

dependencies {
    implementation(project(":feature:editmedia"))
}
