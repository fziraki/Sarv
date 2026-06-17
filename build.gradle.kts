import io.gitlab.arturbosch.detekt.extensions.DetektExtension

plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidMultiplatformLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.detekt) apply false
}

subprojects {
    apply(plugin = "io.gitlab.arturbosch.detekt")

    extensions.configure<DetektExtension> {
        config.setFrom(file("$rootDir/config/detekt.yml"))
        buildUponDefaultConfig = true
        allRules = false
    }

}

tasks.register("detektCheckAll") {
    dependsOn(
        ":shared:detekt",
        ":androidApp:detekt"
    )
}