import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.sqldelight)
}

kotlin {
    listOf(
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }

    android {
        namespace = "abkabk.azbarkon.shared"
        compileSdk =
            libs.versions.android.compileSdk
                .get()
                .toInt()
        minSdk =
            libs.versions.android.minSdk
                .get()
                .toInt()

        compilerOptions {
            jvmTarget = JvmTarget.JVM_17
        }
        androidResources {
            enable = true
        }
        withHostTest {
            isIncludeAndroidResources = true
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.sqldelight.android.driver)
            implementation(libs.koin.android)
            implementation(libs.androidx.work.runtime.ktx)
            implementation(libs.koin.androidx.workmanager)
            implementation(libs.androidx.media3.exoplayer)
            implementation(libs.androidx.media3.common)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
            implementation(libs.sqldelight.native.driver)
        }
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.navigation.compose)
            // 🧠 Ktor Core
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.logging)

            // 🧩 Kotlin Serialization
            implementation(libs.kotlinx.serialization.json)

            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)

            implementation(libs.napier)

            implementation(libs.slf4j.nop)

            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor3)
            implementation(libs.sqldelight.runtime)
            implementation(libs.paging.common)
            implementation(libs.paging.compose)
        }
        val androidHostTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.junit5)
                implementation(libs.assertk)
                implementation(libs.turbine)
                implementation(libs.kotlinx.coroutines.test)
                implementation(libs.paging.testing)
                implementation("org.xerial:sqlite-jdbc:3.50.3.0")
                implementation("app.cash.sqldelight:sqlite-driver:2.3.2")
            }
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
    lintChecks(libs.compose.lint.checks)
}

sqldelight {
    databases {
        create("SarvDatabase") {
            packageName.set("com.sarv.db")
            srcDirs.from("src/commonMain/sqldelight/sarv")
        }
        create("MemorizationDatabase") {
            packageName.set("com.azbarkon.memorization")
            srcDirs.from("src/commonMain/sqldelight/memorization")
        }
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

val generateVersionFile by tasks.registering {
    val versionName = libs.versions.android.versionName.get()
    val outputDir = layout.buildDirectory.dir("generated/version/abkabk/azbarkon/core/util")
    val file = outputDir.map { it.file("AppVersion.kt") }
    outputDir.get().asFile.mkdirs()
    file.get().asFile.writeText(
        """
        |package abkabk.azbarkon.core.util
        |
        |object AppVersion {
        |    const val VERSION_NAME = "$versionName"
        |}
        """.trimMargin(),
    )
    inputs.property("versionName", versionName)
    outputs.file(file)
}

kotlin.targets.all {
    compilations.all {
        compileTaskProvider.configure { dependsOn(generateVersionFile) }
    }
}

kotlin.sourceSets.commonMain {
    kotlin.srcDir(generateVersionFile.map { layout.buildDirectory.dir("generated/version").get() })
}

