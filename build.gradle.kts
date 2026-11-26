@file:Suppress("UnstableApiUsage")

import com.aliucord.gradle.AliucordExtension
import com.android.build.gradle.LibraryExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidExtension

plugins {
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.aliucord.plugin)
}

subprojects {
    val libs = rootProject.libs

    apply {
        plugin(libs.plugins.android.library.get().pluginId)
        plugin(libs.plugins.aliucord.plugin.get().pluginId)
        plugin(libs.plugins.kotlin.android.get().pluginId)
    }

    configure<LibraryExtension> {
        namespace = "me.nyako.aliuplugins"
        compileSdk = 36

        defaultConfig {
            minSdk = 24
        }

        buildFeatures {
            buildConfig = true
            resValues = true
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_21
            targetCompatibility = JavaVersion.VERSION_21
        }
    }

    configure<AliucordExtension> {
        author("nyakowint", 118437263754395652L)
        github("https://github.com/nyakowint/AliuPlugins")
    }

    // Aliucord's gradle plugin writes manifest.json without declaring its fields as task inputs,
    // so a version-only bump would otherwise leave the old manifest in an "up-to-date" zip
    afterEvaluate {
        val aliucord = extensions.getByType<AliucordExtension>()
        tasks.named("package") {
            inputs.property("pluginVersion", version.toString())
            inputs.property("pluginDescription", description.orEmpty())
            inputs.property("pluginChangelog", aliucord.changelog.orElse(""))
            inputs.property("pluginChangelogMedia", aliucord.changelogMedia.orElse(""))
        }
    }

    configure<KotlinAndroidExtension> {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_21
            optIn.add("kotlin.RequiresOptIn")
            freeCompilerArgs.addAll(
                "-Xno-call-assertions",
                "-Xno-param-assertions",
                "-Xno-receiver-assertions",
            )
        }
    }

    dependencies {
        val compileOnly by configurations

        compileOnly(libs.discord)
        compileOnly(libs.aliucord)
        compileOnly(libs.kotlin.stdlib)
    }
}
