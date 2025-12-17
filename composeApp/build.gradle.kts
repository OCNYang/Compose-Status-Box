import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    kotlin("multiplatform")
    id("com.android.application")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.serialization")
}

kotlin {
    // Apply default hierarchy template
    applyDefaultHierarchyTemplate()

    // Android Target
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    // Desktop Target (JVM)
    jvm("desktop") {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    // Web (Wasm) Target - Modern browsers
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser {
            val rootDirPath = project.rootDir.path
            val projectDirPath = project.projectDir.path
            commonWebpackConfig {
                outputFileName = "composeApp.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    static = (static ?: mutableListOf()).apply {
                        // Serve sources to debug inside browser
                        add(rootDirPath)
                        add(projectDirPath)
                    }
                }
            }
        }
        binaries.executable()
    }

    // Web (JS) Target - Legacy browser fallback
    js(IR) {
        browser {
            commonWebpackConfig {
                outputFileName = "composeApp.js"
            }
        }
        binaries.executable()
    }

    sourceSets {
        // Common dependencies
        commonMain.dependencies {
            implementation(project(":status-box-library"))
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.materialIconsExtended)

            // Navigation for Compose Multiplatform
            implementation("org.jetbrains.androidx.navigation:navigation-compose:2.8.0-alpha10")

            // Paging for Compose Multiplatform
            implementation("androidx.paging:paging-compose:3.4.0-alpha04")

            // Kotlinx Serialization
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.7.3")
        }

        // Android dependencies
        androidMain.dependencies {
            implementation("androidx.activity:activity-compose:1.9.0")
            implementation("androidx.core:core-ktx:1.13.1")
            implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.0")
        }

        // Desktop dependencies
        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
            }
        }

        // Web targets automatically inherit from commonMain
    }
}

// Android configuration
android {
    namespace = "com.ocnyang.compose_status_box_demo"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.ocnyang.compose_status_box_demo"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

// Desktop application configuration
compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "StatusBox Demo"
            packageVersion = "1.0.0"
        }
    }
}
