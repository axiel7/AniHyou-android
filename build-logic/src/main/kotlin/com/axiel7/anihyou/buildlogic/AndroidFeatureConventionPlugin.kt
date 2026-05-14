package com.axiel7.anihyou.buildlogic

import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.plugin.compose")
            }

            val libs = extensions.getByType<VersionCatalogsExtension>().find("libs").get()

            extensions.configure<LibraryExtension> {
                compileSdk = libs.findVersion("android-compileSdk").get().requiredVersion.toInt()

                defaultConfig {
                    minSdk = libs.findVersion("android-minSdk").get().requiredVersion.toInt()
                }

                buildFeatures {
                    compose = true
                }

                compileOptions {
                    isCoreLibraryDesugaringEnabled = true
                    sourceCompatibility = JavaVersion.VERSION_11
                    targetCompatibility = JavaVersion.VERSION_11
                }
            }

            extensions.configure<KotlinAndroidProjectExtension> {
                compilerOptions {
                    jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
                }
            }

            dependencies {
                add("implementation", project(":core:network"))
                add("implementation", project(":core:domain"))
                add("implementation", project(":core:ui"))

                add("implementation", platform(libs.findLibrary("androidx-compose-bom").get()))
                add("implementation", libs.findLibrary("androidx-ui").get())
                add("implementation", libs.findLibrary("androidx-ui-tooling-preview").get())
                add("implementation", libs.findLibrary("androidx-material3").get())

                add("implementation", platform(libs.findLibrary("koin-bom").get()))
                add("implementation", libs.findLibrary("koin-compose").get())

                add("coreLibraryDesugaring", libs.findLibrary("desugar_jdk_libs").get())
            }
        }
    }
}
