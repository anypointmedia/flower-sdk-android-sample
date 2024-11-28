plugins {
    kotlin("android")
    id("com.android.application")
}

android {
    namespace = "tv.anypoint.flower.sdk.reference.android.vod"
    compileSdk = 32

    defaultConfig {
        applicationId = "tv.anypoint.flower.sdk.reference.android.vod"
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
    implementation("com.google.android.exoplayer:exoplayer-core:2.17.1")
    implementation("com.google.android.exoplayer:exoplayer-ui:2.17.1")
    implementation("com.google.android.exoplayer:exoplayer-dash:2.17.1")
    implementation("com.google.android.exoplayer:exoplayer-hls:2.17.1")
    implementation("org.lighthousegames:logging:1.3.0")
}
