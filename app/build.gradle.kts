import com.android.build.api.dsl.AaptOptions
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("kotlin-kapt")
}

android {
    namespace = "com.handson.handson"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.handson.handson"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        mlModelBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }


}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation("androidx.compose.material:material-icons-extended:1.5.4")
    implementation("androidx.navigation:navigation-runtime-ktx:2.7.2")
    implementation("androidx.navigation:navigation-compose:2.7.2")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.camera:camera-view:1.3.0")
    implementation("androidx.camera:camera-core:1.3.0")
    implementation("androidx.camera:camera-camera2:1.3.0")
    implementation("androidx.camera:camera-lifecycle:1.3.0")
    implementation("androidx.camera:camera-video:1.3.0")
    implementation("androidx.camera:camera-extensions:1.3.0")

    implementation("com.google.accompanist:accompanist-permissions:0.33.2-alpha")
    implementation("io.coil-kt:coil-compose:1.4.0")

    implementation("com.google.mediapipe:solution-core:latest.release")
    implementation("com.google.mediapipe:hands:latest.release")
    implementation("org.tensorflow:tensorflow-lite-support:latest.release")
    //implementation("org.tensorflow:tensorflow-lite:latest.release")
    implementation("org.tensorflow:tensorflow-lite:2.14.0")

    implementation("org.tensorflow:tensorflow-lite-metadata:latest.release")
    // implementation("org.tensorflow:tensorflow-lite-gpu:latest.release")
    implementation("com.google.android.gms:play-services-tflite-gpu:16.2.0")
    implementation("com.google.firebase:firebase-ml-model-interpreter:22.0.4")



    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")


    implementation(platform("com.google.firebase:firebase-bom:32.7.1"))
    implementation("com.google.firebase:firebase-ml-modeldownloader")

    implementation("com.airbnb.android:lottie-compose:6.4.0")


    val room_version = "2.6.1"

    implementation("androidx.room:room-runtime:$room_version")
    annotationProcessor("androidx.room:room-compiler:$room_version")
    kapt ("androidx.room:room-compiler:$room_version")
    // optional - Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:$room_version")




}