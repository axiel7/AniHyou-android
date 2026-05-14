plugins {
    alias(libs.plugins.anihyou.feature)
}

val appPackageName: String by rootProject.extra

android {
    namespace = "$appPackageName.widget"
}

dependencies {
    implementation(libs.androidx.glance.appwidget)
    implementation(libs.androidx.glance.preview)
    implementation(libs.androidx.glance.appwidget.preview)

    implementation(libs.androidx.datastore.preferences)
}
