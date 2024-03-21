pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven(url = "https://maven.anypoint.tv/repository/public")
        google()
        mavenCentral()
    }
}

rootProject.name = "FlowerOTTApplication"
include(":app")
