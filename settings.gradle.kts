// settings.gradle.kts
pluginManagement {
    repositories {
        google()
        // Try multiple Maven repositories for better connectivity
        mavenCentral()
        maven { url = uri("https://repo1.maven.org/maven2/") }
        maven { url = uri("https://jcenter.bintray.com/") }
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        // Try multiple Maven repositories for better connectivity
        mavenCentral()
        maven { url = uri("https://repo1.maven.org/maven2/") }
        maven { url = uri("https://jcenter.bintray.com/") }
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "ModernTodo"
include(":app")