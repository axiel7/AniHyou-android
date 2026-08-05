import java.util.Properties

plugins {
    alias(libs.plugins.anihyou.feature)
}

val appPackageName: String by rootProject.extra

val versionProps = Properties().also {
    it.load(project.rootProject.file("version.properties").reader())
}

android {
    namespace = "$appPackageName.feature.settings"

    buildFeatures {
        buildConfig = true
    }

    buildTypes.all {
        buildConfigField("int", "VERSION_CODE", versionProps.getProperty("code"))
        buildConfigField("String", "VERSION_NAME", "\"${versionProps.getProperty("name")}\"")
    }
}

dependencies {
    implementation(project(":feature:worker"))

    implementation(libs.androidx.work.runtime)
    implementation(libs.accompanist.permissions)
}
