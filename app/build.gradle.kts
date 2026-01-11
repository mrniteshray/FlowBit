plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "xcom.niteshray.xapps.xblockit"
    compileSdk = 35

    defaultConfig {
        applicationId = "xcom.niteshray.xapps.xblockit"
        minSdk = 24
        targetSdk = 35
        versionCode = 21
        versionName = "1.6"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            
            // Use ProGuard rules for code optimization
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            
            // Debuggable disabled for release builds
            isDebuggable = false
            
            // Enable R8 full mode for better optimization
            // R8 is enabled by default in newer AGP versions
        }
        debug {
            // Debug build optimizations
            isMinifyEnabled = false
            isDebuggable = true
            versionNameSuffix = "-DEBUG"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation("androidx.compose.material:material-icons-extended:1.7.6")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.androidx.navigation.compose)
    implementation("androidx.media3:media3-exoplayer:1.6.1")
    implementation("androidx.media3:media3-exoplayer-dash:1.6.1")
    implementation("androidx.media3:media3-ui:1.6.1")
    implementation("androidx.media3:media3-ui-compose:1.6.1")

    implementation("com.android.billingclient:billing:8.3.0")

    implementation("androidx.glance:glance-appwidget:1.1.1")
// For interop APIs with Material 3
    implementation("androidx.glance:glance-material3:1.1.1")

    implementation("com.revenuecat.purchases:purchases:9.19.0")

    // RevenueCat Paywall UI for Compose
    implementation("com.revenuecat.purchases:purchases-ui:9.19.0")

}