plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt") // ✅ تأكد من إضافة هذا السطر
}

android {
    namespace = "com.example.nota_app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.nota_app"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }

        // Enable desugaring for Java 8+ API usage

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
}

dependencies {
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.5.0")

    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation ("androidx.room:room-runtime:2.5.0")
    annotationProcessor ("androidx.room:room-compiler:2.5.0")
    kapt ("androidx.room:room-compiler:2.5.0")
    implementation("androidx.appcompat:appcompat:1.6.0")
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.work:work-runtime:2.7.1")
    implementation ("org.jetbrains.kotlin:kotlin-stdlib:1.7.0")
    //implementation 'com.jakewharton.threetenabp:threetenabp:1.3.1'
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}