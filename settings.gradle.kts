@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        maven {
            name = "aliucord"
            url = uri("https://maven.aliucord.com/releases")
        }
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            name = "aliucord"
            url = uri("https://maven.aliucord.com/releases")
        }
    }
}

rootProject.name = "AliuPlugins"

// Include every plugin directory (anything with a build script) except the template
rootDir
    .listFiles { file -> file.isDirectory && file.name != "Template" && file.resolve("build.gradle.kts").exists() }!!
    .forEach { include(":${it.name}") }
