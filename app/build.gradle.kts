plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.delhoume.flashyflash"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.delhoume.flashyflash"
        minSdk = 24
        targetSdk = 34
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
        viewBinding = true
    }
}

dependencies {
// Accompanist Permissions for handling runtime permissions
    implementation(libs.camera.mlkit.vision)
    implementation(libs.mlkit.barcode.scanning)

   implementation(libs.accompanistPermissions)
   implementation ("com.google.accompanist:accompanist-permissions:0.37.0")
    implementation("com.google.mlkit:barcode-scanning:17.3.0")
    implementation("com.dragselectcompose:dragselect:2.4.1")
    implementation ("com.afollestad:drag-select-recyclerview:2.4.0")
    implementation ("com.github.nanihadesuka:LazyColumnScrollbar:2.2.0")

    implementation("io.coil-kt.coil3:coil-compose:3.0.4")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.0.4")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    implementation("androidx.core:core-splashscreen:1.0.1")
    //
    implementation ("com.google.mlkit:camera:16.0.0-beta3")
    implementation("androidx.camera:camera-camera2:1.0.2")
    implementation ("androidx.camera:camera-lifecycle:1.0.2")
    implementation ("androidx.camera:camera-view:1.0.0-alpha31")

    // Zxing
    implementation("com.journeyapps:zxing-android-embedded:4.1.0")
    implementation ("com.google.zxing:core:3.4.0")
    implementation("com.darkrockstudios:mpfilepicker:3.1.0")
    implementation(libs.compose.qr.code)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.navigation.compose)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
     androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
