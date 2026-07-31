import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.extensions.DetektExtension

val composeRulesDetekt = libs.compose.rules.detekt

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

    dependencies {
        project.dependencies.add("detektPlugins", composeRulesDetekt)
    }

    extensions.configure<DetektExtension> {
        config.setFrom(file("$rootDir/config/detekt.yml"))
        baseline = file("$rootDir/config/detekt-baseline.xml")
        buildUponDefaultConfig = true
        allRules = false
    }
}

gradle.projectsEvaluated {
    allprojects {
        tasks.withType<Detekt>().configureEach {
            setSource(source.files.filter { !it.path.contains("build${File.separator}generated") })
        }
    }
}

tasks.register("detektCheckAll") {
    dependsOn(
        ":shared:detektMetadataCommonMain",
        ":shared:detektAndroidMain",
        ":androidApp:detekt"
    )
}