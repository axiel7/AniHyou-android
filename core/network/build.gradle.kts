plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
    alias(libs.plugins.apollo)
    alias(libs.plugins.jetbrains.kotlin.serialization)
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
    }
}

dependencies {
    implementation(project(":core:base"))

    implementation(libs.apollo.runtime)
    implementation(libs.apollo.normalized.cache)
    api(libs.apollo.api)

    implementation(libs.kotlinx.serialization.json)

    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)
}

apollo {
    val appPackageName: String by rootProject.extra
    generateSourcesDuringGradleSync.set(false)
    service("service") {
        packageName.set("$appPackageName.core.network")
        generateFragmentImplementations.set(true)
        mapScalarToKotlinInt("FuzzyDateInt")
        mapScalar(
            "CountryCode",
            "$appPackageName.core.network.api.model.CountryOfOriginDto",
            "$appPackageName.core.network.api.model.CountryOfOriginDto.countryOfOriginAdapter",
        )
        introspection {
            endpointUrl.set("https://graphql.anilist.co")
            schemaFile.set(file("src/main/graphql/schema.graphqls"))
        }
    }
}
