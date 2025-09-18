import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose.hot.reload)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    jvm()

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.gifCompose)

            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)

            implementation(libs.compose.material3.adaptive)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.filekit.core)
            implementation(libs.filekit.dialogs)
            implementation(libs.tiamat)
            implementation(libs.zoomable)
        }
        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)

            implementation(compose.preview)
        }
        jvmMain.dependencies {
            implementation(libs.skiko)
            implementation(libs.kotlinx.coroutines.swing)
            implementation(compose.desktop.currentOs)
        }
        wasmJsMain.dependencies {
            implementation(libs.skiko)
        }
    }
}

android {
    namespace = "io.github.composegears.pixelart"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "io.github.composegears.pixelart"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "LICENSE-EDL-1.0.txt"
            excludes += "LICENSE-EPL-1.0.txt"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

compose.desktop {
    application {
        mainClass = "io.github.composegears.pixelart.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "io.github.composegears.pixelart"
            packageVersion = "1.0.0"

            linux {
                modules("jdk.security.auth")
            }
        }
    }
}
