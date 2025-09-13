import io.gitlab.arturbosch.detekt.DetektPlugin

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.compose.hot.reload) apply false
    alias(libs.plugins.compose.multiplatform) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.detekt)
    alias(libs.plugins.kotlin.multiplatform) apply false
}

detekt {
    config.from(files("config/detekt/detekt.yml"))
    buildUponDefaultConfig = true
    parallel = true
}

allprojects {
    apply<DetektPlugin>()
    detekt {
        buildUponDefaultConfig = true
        parallel = true
        autoCorrect = true
        source.from(
            files(
                "src/commonMain/kotlin",
                "src/commonTest/kotlin",
                "src/jvmMain/kotlin",
                "src/desktopMain/kotlin",
                "src/androidMain/kotlin",
                "src/wasmJsMain/kotlin",
            )
        )
    }

    dependencies {
        detektPlugins(rootProject.project.libs.detekt.compose)
        detektPlugins(rootProject.project.libs.detekt.formatting)
    }
}
