plugins {
    alias(libs.plugins.anihyou.feature)
}

val appPackageName = rootProject.extra["appPackageName"] as String

android {
    namespace = "$appPackageName.feature.staffdetails"
}

dependencies {
    implementation(project(":feature:editmedia"))
}
