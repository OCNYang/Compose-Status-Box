import org.gradle.kotlin.dsl.api
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("com.android.library")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
    id("maven-publish")
}

kotlin {
    // Apply default hierarchy template for better source set organization
    applyDefaultHierarchyTemplate()

    // Android Target
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
        publishLibraryVariants("release", "debug")
    }

    // Desktop Target (JVM)
    jvm("desktop") {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    // iOS Targets
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "StatusBoxLibrary"
            isStatic = true
        }
    }

    // Conditionally enable Web targets (skip on JitPack to avoid Node.js GLIBC issues)
    val isJitpackBuild = project.hasProperty("jitpackBuild")
    if (!isJitpackBuild) {
        // Web (Wasm) Target - Modern browsers
        @OptIn(ExperimentalWasmDsl::class)
        wasmJs {
            browser()
        }

        // Web (JS) Target - Legacy browser fallback
        js(IR) {
            browser()
        }
    }

    sourceSets {
        // Common Main
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation("androidx.paging:paging-compose:3.4.0-alpha04")
        }

        // Android Main
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(compose.uiTooling)
        }

        // Desktop Main
        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
            }
        }

        // iOS Main - no extra dependencies needed currently

        // Web targets (wasmJs and js) are automatically configured by the default hierarchy template
        // They will inherit from commonMain automatically
    }
}

android {
    namespace = "com.ocnyang.status_box"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }
}

compose.resources {
    publicResClass = true
    packageOfResClass = "com.ocnyang.status_box.resources"
    generateResClass = auto
}

publishing {
    publications {
        withType<MavenPublication> {
            groupId = "com.github.ocnyang"
            artifactId = "compose-status-box-kmp"
            version = "2.0.0"
        }
    }
}
