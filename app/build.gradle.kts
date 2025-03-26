plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.kishan.foodappjetpack"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.kishan.foodappjetpack"
        minSdk = 24
        targetSdk = 35
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
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.compose.material.icons)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.coil.compose)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.snapper)

    testImplementation(libs.junit)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.inline)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.arch.core.testing)

//    // Material Icons Extended (for FilterList and other icons)
//    implementation ("androidx.compose.material:material-icons-extended:1.5.0")
//
//    // ViewModel with Compose
//    implementation ("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")
//
//    // Retrofit for networking
//    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
//    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
//
//    // Coil for image loading
//    implementation ("io.coil-kt:coil-compose:2.4.0")
//
//    // Coroutines
//    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
//
//    implementation ("dev.chrisbanes.snapper:snapper:0.3.0")
//
//    // Unit Testing
//    testImplementation ("junit:junit:4.13.2")
//    testImplementation ("org.mockito:mockito-core:5.12.0")
//    testImplementation ("org.mockito:mockito-inline:5.2.0") // For mocking final classes (like Retrofit)
//    testImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3") // For testing coroutines
//    testImplementation ("androidx.arch.core:core-testing:2.2.0") // For testing LiveData/ViewModel
}