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

            implementation(libs.kamel.image)
            implementation(libs.sqldelight.runtime)
            implementation(libs.paging.common)
            implementation(libs.paging.compose)
            implementation(libs.reorderable)
        }
        val androidHostTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.junit5)
                implementation(libs.assertk)
                implementation(libs.turbine)
                implementation(libs.kotlinx.coroutines.test)
                implementation(libs.paging.testing)
                implementation("org.xerial:sqlite-jdbc:3.49.1.0")
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
}

sqldelight {
    databases {
        create("AzbarKonDatabase") {
            packageName.set("com.azbarkon.db")
            srcDirs.from("src/commonMain/sqldelight/azbarkon")
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

