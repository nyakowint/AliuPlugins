import com.aliucord.gradle.AliucordExtension
import com.android.build.gradle.BaseExtension

buildscript {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.aliucord.com/snapshots")
        gradlePluginPortal() // remove when gradle 8
        maven("https://jitpack.io")
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.1.3")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.31")
        classpath("com.github.Aliucord:gradle:main-SNAPSHOT")
        //classpath("com.gradleup.shadow:shadow-gradle-plugin:8.3.8")
        classpath("com.github.johnrengelman.shadow:com.github.johnrengelman.shadow.gradle.plugin:7.1.2") // For Gradle 7 compat (allegedly)
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.aliucord.com/snapshots")
    }
}

fun Project.android(configuration: BaseExtension.() -> Unit) =
    extensions.getByName<BaseExtension>("android").configuration()

fun Project.aliucord(configuration: AliucordExtension.() -> Unit) =
    extensions.getByName<AliucordExtension>("aliucord").configuration()

subprojects {
    apply(plugin = "com.android.library")
    apply(plugin = "com.aliucord.gradle")
    apply(plugin = "kotlin-android")

    
    aliucord {
        author("nyakowint", 118437263754395652L)
        updateUrl.set("https://raw.githubusercontent.com/nyakowint/AliuPlugins/builds/updater.json")
        buildUrl.set("https://raw.githubusercontent.com/nyakowint/AliuPlugins/builds/%s.zip")
    }

    android {
        compileSdkVersion(31) // fuck you google 

        defaultConfig {
            minSdk = 24
            targetSdk = 31  // fuck you google
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_11
            targetCompatibility = JavaVersion.VERSION_11
        }

        tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
            kotlinOptions {
                jvmTarget = "11"
                freeCompilerArgs = freeCompilerArgs +
                        "-Xno-call-assertions" +
                        "-Xno-param-assertions" +
                        "-Xno-receiver-assertions"
            }
        }
    }

    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }

    dependencies {
        val discord by configurations
        val implementation by configurations

        discord("com.discord:discord:aliucord-SNAPSHOT")
        implementation("com.aliucord:Aliucord:2.5.0")
    }
}

task<Delete>("clean") {
    delete(rootProject.buildDir)
}
