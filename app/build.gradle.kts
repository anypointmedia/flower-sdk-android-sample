plugins {
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.android.application)
}

android {
    namespace = "tv.anypoint.flower.reference.android"
    compileSdk = 34
    defaultConfig {
        applicationId = "tv.anypoint.flower.reference.android"
        minSdk = 21
        versionCode = 1
        versionName = "1.0"
        setProperty("archivesBaseName", "flower-reference-app_v${versionName}(${versionCode})")

        multiDexEnabled = true
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true

        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    packagingOptions {
        resources.excludes.add("META-INF/DEPENDENCIES.md")
        resources.excludes.add("META-INF/NOTICE.md")
        resources.excludes.add("META-INF/LICENSE.md")
        resources.excludes.add("META-INF/LICENSE.txt")
        resources.excludes.add("META-INF/NOTICE.txt")
        resources.excludes.add("META-INF/ASL2.0")
        resources.excludes.add("META-INF/INDEX.LIST")
        resources.excludes.add("build.xml")
    }
}

dependencies {
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")

    val exoplayerVer = "2.19.1"

    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.leanback:leanback:1.0.0")
    implementation("androidx.appcompat:appcompat:1.4.1")

    implementation("com.github.bumptech.glide:glide:4.13.2")

    implementation("com.google.android.exoplayer:exoplayer-core:$exoplayerVer")
    implementation("com.google.android.exoplayer:exoplayer-ui:$exoplayerVer")
    implementation("com.google.android.exoplayer:exoplayer-dash:$exoplayerVer")
    implementation("com.google.android.exoplayer:exoplayer-hls:$exoplayerVer")

    implementation("flower-sdk:sdk-android-ott:1.0.15")

    testImplementation("org.junit.jupiter:junit-jupiter:5.9.0")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.0")
    testImplementation("org.mockito:mockito-inline:4.8.0")
    testImplementation("io.kotest:kotest-runner-junit5:5.5.4")
}
