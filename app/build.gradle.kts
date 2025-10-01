plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.feedlink.feedlink"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.feedlink.feedlink"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

//dependencies {
//
//    implementation(libs.androidx.core.ktx)
//    implementation(libs.androidx.lifecycle.runtime.ktx)
//    implementation(libs.androidx.activity.compose)
//    implementation(platform(libs.androidx.compose.bom))
//    implementation(libs.androidx.ui)
//    implementation(libs.androidx.ui.graphics)
//    implementation(libs.androidx.ui.tooling.preview)
//    implementation(libs.androidx.material3)
//    implementation(libs.androidx.compose.foundation.layout)
//    implementation(libs.androidx.compose.ui)
//    implementation(libs.androidx.compiler)
//    testImplementation(libs.junit)
//    androidTestImplementation(libs.androidx.junit)
//    androidTestImplementation(libs.androidx.espresso.core)
//    androidTestImplementation(platform(libs.androidx.compose.bom))
//    androidTestImplementation(libs.androidx.ui.test.junit4)
//    debugImplementation(libs.androidx.ui.tooling)
//    debugImplementation(libs.androidx.ui.test.manifest)
//
//    implementation("com.squareup.retrofit2:retrofit:2.9.0")
//    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
//    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")
//    implementation ("androidx.compose.ui:ui")
//    implementation ("androidx.compose.ui:ui-tooling-preview")
//    implementation ("androidx.compose.material3:material3")
//    implementation ("androidx.activity:activity-compose:1.8.2")
//    implementation ("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
//    implementation ("androidx.navigation:navigation-compose:2.7.5")
//    implementation ("androidx.compose.runtime:runtime-livedata:1.5.4")
//    implementation(platform("androidx.compose:compose-bom:2024.06.00"))
//
//    implementation("androidx.compose.material:material-icons-extended:<compose_version>")
//
//    implementation("androidx.security:security-crypto:1.1.0-alpha03")
//
//    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")
//    implementation("androidx.compose.material3:material3:<latest-version>")
//    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:<latest-version>")
//    implementation("androidx.lifecycle:lifecycle-livedata-ktx:<latest-version>")
//    implementation("io.coil-kt:coil-compose:2.2.2")
//    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")
//    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.5")
//    implementation("androidx.compose.runtime:runtime-livedata:1.5.0")
//    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.5")
//    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.3")
//    implementation("com.google.android.gms:play-services-location:21.1.0")
//
//    // Retrofit for API calls
//// ViewModel and LiveData
//// Navigation
//// Location services
//// Image loading
//    implementation("io.coil-kt:coil-compose:2.2.2")
//// Permissions handling
//    implementation("com.google.accompanist:accompanist-permissions:0.28.0")
//}

dependencies {
    // AndroidX Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.androidx.compose.foundation)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Core Library Desugaring
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.3")

    // Retrofit & Networking
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Compose
    implementation(platform("androidx.compose:compose-bom:2024.06.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended:1.6.7")

    // Lifecycle & ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.compose.runtime:runtime-livedata:1.6.7")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.5")

    // Image Loading
    implementation("io.coil-kt:coil-compose:2.5.0")

    // Location Services with exclusion to avoid Guava conflict
    implementation("com.google.android.gms:play-services-location:21.1.0") {
        exclude(group = "com.google.guava", module = "listenablefuture")
    }

    // Security
    implementation("androidx.security:security-crypto:1.1.0-alpha06")

    // Permissions Handling
    implementation("com.google.accompanist:accompanist-permissions:0.32.0")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("io.insert-koin:koin-android:3.5.3")
    implementation("io.insert-koin:koin-androidx-compose:3.5.3")
    implementation("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.16")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
}