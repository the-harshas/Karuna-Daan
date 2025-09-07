plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("com.google.gms.google-services")
    id("org.jetbrains.kotlin.kapt")
    id("com.google.dagger.hilt.android")
    id ("kotlin-kapt")
    id ("dagger.hilt.android.plugin")
//    id("com.google.devtools.ksp")
}
//apply plugin: 'com.android.application'
//apply plugin: 'com.google.dagger.hilt.android'

android {
    namespace = "com.example.karunadaan"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.karunadaan"
        minSdk = 24
        targetSdk = 29
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
    buildToolsVersion = "34.0.0"
}
dependencies {
// Core AndroidX libraries
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)


    // Firebase libraries
    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
    implementation(libs.firebase.database)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.storage.ktx)
    implementation("com.google.firebase:firebase-auth")

    // Glide (image loading library)
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation(libs.androidx.lifecycle.viewmodel.android)
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    // AndroidX Room (Database library)
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1") // For Kotlin annotation processing

    // WorkManager
    implementation("androidx.work:work-runtime-ktx:2.6.0")

    // Testing libraries
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Google Play Services
    implementation("com.google.android.gms:play-services-auth:21.3.0")


    // Annotation
    implementation("androidx.annotation:annotation:1.9.1")
    compileOnly("org.jetbrains:annotations:23.0.0")

    kapt ("org.jetbrains.kotlinx:kotlinx-metadata-jvm:0.9.0")

    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")

    // If this project uses any Kotlin source, use Kotlin Symbol Processing (KSP)
    // See Add the KSP plugin to your project
    //ksp("androidx.room:room-compiler:$room_version")

    // If this project only uses Java source, use the Java annotationProcessor
    // No additional plugins are necessary
    annotationProcessor("androidx.room:room-compiler:$room_version")

    // optional - Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:$room_version")

    // optional - RxJava2 support for Room
    implementation("androidx.room:room-rxjava2:$room_version")

    // optional - RxJava3 support for Room
    implementation("androidx.room:room-rxjava3:$room_version")

    // optional - Guava support for Room, including Optional and ListenableFuture
    implementation("androidx.room:room-guava:$room_version")

    // optional - Test helpers
    testImplementation("androidx.room:room-testing:$room_version")

    // optional - Paging 3 Integration
    implementation("androidx.room:room-paging:$room_version")
    
    //coroutine
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.9")
    var nav_version = "2.3.5"

    // Navigation Component
    implementation ("androidx.navigation:navigation-fragment-ktx:$nav_version")
    implementation ("androidx.navigation:navigation-ui-ktx:$nav_version")
//
//    implementation("com.google.dagger:hilt-android:2.51.1")
//    kapt("com.google.dagger:hilt-android-compiler:2.51.1")
    implementation ("com.google.dagger:hilt-android:2.51.1")
    annotationProcessor ("com.google.dagger:hilt-compiler:2.51.1")

    // For instrumentation tests
    androidTestImplementation  ("com.google.dagger:hilt-android-testing:2.51.1")
    androidTestAnnotationProcessor ("com.google.dagger:hilt-compiler:2.51.1")

    // For local unit tests
    testImplementation ("com.google.dagger:hilt-android-testing:2.51.1")
    testAnnotationProcessor ("com.google.dagger:hilt-compiler:2.51.1")
    kapt ("androidx.hilt:hilt-compiler:1.2.0")
    implementation ("androidx.hilt:hilt-navigation-compose:1.2.0")

    val work_version = "2.10.0"
    // Kotlin + coroutines
    implementation("androidx.work:work-runtime-ktx:$work_version")

    implementation ("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.16.0")

    implementation ("com.google.android.gms:play-services-location:21.0.1")


}
kapt {
    correctErrorTypes = true
}


