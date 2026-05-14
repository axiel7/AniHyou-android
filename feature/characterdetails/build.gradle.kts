plugins {
    alias(libs.plugins.anihyou.feature)
}

val appPackageName: String by rootProject.extra

android {
    namespace = "$appPackageName.feature.characterdetails"
}

dependencies {
    implementation(project(":feature:editmedia"))
}
