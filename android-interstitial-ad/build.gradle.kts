plugins {
    kotlin("android")
    id("com.android.application")
}

android {
    namespace = "tv.anypoint.flower.sdk.reference.android.interstitial_ad"
    compileSdk = 32

    defaultConfig {
        applicationId = "tv.anypoint.flower.sdk.reference.android.interstitial_ad"
        minSdk = 17
        versionCode = 1
        versionName = "1.0"

        multiDexEnabled = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    implementation("flower-sdk:sdk-android-ott:1.0.31-rc1")
    implementation("org.lighthousegames:logging:1.3.0")
}