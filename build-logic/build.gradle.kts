plugins {
    `kotlin-dsl`
}

gradlePlugin {
    plugins {
        register("androidFeature") {
            id = "anihyou.android.feature"
            implementationClass = "com.axiel7.anihyou.buildlogic.AndroidFeatureConventionPlugin"
        }
    }
}

dependencies {
    implementation(libs.android.gradlePlugin)
    implementation(libs.kotlin.gradlePlugin)
    implementation(libs.compose.gradlePlugin)
}
