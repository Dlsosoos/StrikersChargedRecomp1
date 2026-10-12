plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

// Ative o motor nativo com:  gradle assembleDebug -PenableNative
val enableNative = project.hasProperty("enableNative")

android {
    namespace = "com.pedro.strikersrecomp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.pedro.strikersrecomp"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "0.1.0"
        if (enableNative) {
            ndk { abiFilters += listOf("arm64-v8a") }
        }
    }

    buildTypes {
        release { isMinifyEnabled = false }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }

    buildFeatures { compose = true }
    composeOptions { kotlinCompilerExtensionVersion = "1.5.14" }

    if (enableNative) {
        externalNativeBuild {
            cmake { path = file("src/main/cpp/CMakeLists.txt") }
        }
    }
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2024.06.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation("androidx.core:core-ktx:1.13.1")
}
