plugins {
    alias(libs.plugins.anihyou.feature)
}

val appPackageName = rootProject.extra["appPackageName"] as String

android {
    namespace = "$appPackageName.feature.explore"
}

dependencies {
    implementation(project(":feature:editmedia"))
    implementation(project(":feature:genrestags"))
}
