plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.talhaatif.tickojet"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.talhaatif.tickojet"
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

    // Add this block to set the JVM target for Kotlin
    kotlinOptions {
        jvmTarget = "11"
    }


    buildFeatures{
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation ("com.github.denzcoskun:ImageSlideshow:0.1.2")
    implementation ("de.hdodenhof:circleimageview:3.1.0")
    implementation ("com.google.android.material:material:1.11.0")
    implementation ("com.squareup.retrofit2:retrofit:2.11.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.11.0")

    implementation ("androidx.datastore:datastore-preferences:1.0.0")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")

    implementation ("androidx.fragment:fragment-ktx:1.6.1")

    implementation ("com.github.bumptech.glide:glide:4.16.0")

    // Lottie Animation for Beautiful Loading
    implementation ("com.airbnb.android:lottie:6.1.0")

    // carousel

    implementation ("com.github.sparrow007:CarouselRecyclerview:1.2.5")

    implementation ("androidx.core:core-splashscreen:1.0.1")

    implementation ("org.java-websocket:Java-WebSocket:1.5.2")
    implementation ("com.squareup.okhttp3:okhttp:4.9.3")







    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.core.animation)
    implementation(libs.datastore.core.android)
    implementation(libs.datastore.preferences.core.jvm)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}