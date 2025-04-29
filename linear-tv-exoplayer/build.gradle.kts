repositories {
    google()
    mavenCentral()
    maven("https://maven.anypoint.tv/repository/public")
}

plugins {
    kotlin("android")
    id("com.android.application")
}

android {
    namespace = "tv.anypoint.flower.sdk.reference.android.linear_tv"
    compileSdk = 34

    defaultConfig {
        applicationId = "tv.anypoint.flower.sdk.reference.android.linear_tv"
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
        debug {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")

    implementation("com.google.android.exoplayer:exoplayer-core:2.17.1")
    implementation("com.google.android.exoplayer:exoplayer-ui:2.17.1")
    implementation("com.google.android.exoplayer:exoplayer-hls:2.17.1")
    implementation("com.google.android.exoplayer:exoplayer-dash:2.17.1")

    implementation("flower-sdk:sdk-android-ott:1.2.1")
}
