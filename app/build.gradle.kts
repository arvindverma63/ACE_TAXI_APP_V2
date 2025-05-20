plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.app.ace_taxi_v2"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.app.ace_taxi_v2"
        minSdk = 24
        targetSdk = 34
        versionCode = 82
        versionName = "9.2"
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
}

dependencies {
    // AndroidX Core Libraries
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.swiperefreshlayout)

    // Google Play Services (deduplicated)
    implementation("com.google.android.gms:play-services-maps:19.0.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("com.google.maps.android:android-maps-utils:3.4.0")

    // Firebase
    implementation(libs.firebase.auth)
    implementation(libs.firebase.messaging)

    // Work Manager
    implementation(libs.work.runtime)

    // Networking
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.1")

    // GIF Support
    implementation("pl.droidsonroids.gif:android-gif-drawable:1.2.25")

    // Sentry for crash reporting
    implementation("io.sentry:sentry-android:8.2.0")
    implementation ("androidx.palette:palette:1.0.0")
    implementation(libs.engage.core)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}