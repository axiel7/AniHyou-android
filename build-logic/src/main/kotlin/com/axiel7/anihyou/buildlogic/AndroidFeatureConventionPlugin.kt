package com.axiel7.anihyou.buildlogic

import com.android.build.api.dsl.LibraryExtension
import com.skydoves.compose.stability.gradle.StabilityAnalyzerExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

@Suppress("unused")
class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.plugin.compose")
                apply("io.insert-koin.compiler.plugin")
                apply("com.github.skydoves.compose.stability.analyzer")
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
                add("implementation", libs.findLibrary("koin-annotations").get())
                add("implementation", libs.findLibrary("koin-compose").get())
                add("implementation", libs.findLibrary("koin-compose-viewmodel").get())
                add("implementation", libs.findLibrary("koin-compose-navigation3").get())

                add("implementation", libs.findLibrary("collections-immutable").get())

                add("coreLibraryDesugaring", libs.findLibrary("desugar_jdk_libs").get())
            }

            extensions.configure<ComposeCompilerGradlePluginExtension> {
                stabilityConfigurationFiles.add(isolated.rootProject.projectDirectory.file("stability_config.conf"))
            }

            extensions.configure<StabilityAnalyzerExtension> {
                stabilityConfigurationFiles.add(isolated.rootProject.projectDirectory.file("stability_config.conf"))
            }
        }
    }
}
