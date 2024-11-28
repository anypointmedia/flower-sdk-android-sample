plugins {
    kotlin("android") version "1.9.22" apply false
    id("com.android.application") version "8.1.3" apply false
}

allprojects {
    repositories {
        maven(url = "https://maven.anypoint.tv/repository/public")
        mavenCentral()
        google()
    }
}
