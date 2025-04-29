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
    namespace = "tv.anypoint.flower.sdk.reference.android.linear_tv.media3exoplayer"
    compileSdk = 34

    defaultConfig {
        applicationId = "tv.anypoint.flower.sdk.reference.android.linear_tv.media3exoplayer"
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

    implementation("androidx.media3:media3-exoplayer:1.0.0")
    implementation("androidx.media3:media3-exoplayer-dash:1.0.0")
    implementation("androidx.media3:media3-exoplayer-hls:1.0.0")
    implementation("androidx.media3:media3-ui:1.0.0")

    implementation("flower-sdk:sdk-android-ott:1.2.1")
}
