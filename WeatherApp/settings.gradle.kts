pluginManagement {
    repositories {
        google() // Bu satırın olduğundan emin olun
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google() // Bu satırın olduğundan emin olun
        mavenCentral()
    }
}

rootProject.name = "WeatherApp"
include(":app")
 