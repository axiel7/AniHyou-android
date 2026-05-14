plugins {
    alias(libs.plugins.anihyou.feature)
}

val appPackageName: String by rootProject.extra

android {
    namespace = "$appPackageName.feature.home"
}

dependencies {
    implementation(project(":feature:editmedia"))
    implementation(project(":feature:login"))
}
