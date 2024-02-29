plugins {
  id("com.android.library")
  id("org.jetbrains.kotlin.android")
  id("kotlin-kapt")
  id("dagger.hilt.android.plugin")
}

android {
  namespace = "com.appcoins.payment_manager.presentation"
  compileSdk = 34

  defaultConfig {
    minSdk = 26

    consumerProguardFiles("consumer-rules.pro")
  }

  buildTypes {
    release {
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }

  lint {
    abortOnError = false
  }

  buildFeatures {
    // Enables Jetpack Compose for this module
    compose = true
    buildConfig = true
  }

  composeOptions {
    kotlinCompilerExtensionVersion = "1.4.8"
  }
}

dependencies {
  implementation("androidx.core:core-ktx:1.10.1")
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.8.22")

  // coroutines
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")

  //logger
  implementation("com.jakewharton.timber:timber:5.0.1")

  //Hilt
  implementation("com.google.dagger:hilt-android:2.46.1")
  kapt("com.google.dagger:hilt-compiler:2.46.1")
  kapt("androidx.hilt:hilt-compiler:1.0.0")

  implementation("androidx.appcompat:appcompat:1.6.1")
  implementation("androidx.activity:activity-ktx:1.7.2")
  implementation("com.google.android.material:material:1.11.0")

  implementation("androidx.navigation:navigation-compose:2.7.5")
  implementation("androidx.hilt:hilt-navigation-compose:1.0.0")

  implementation("androidx.compose.material:material:1.5.4")
  implementation("androidx.compose.animation:animation:1.5.4")
  implementation("androidx.compose.ui:ui-tooling:1.5.4")
  implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")
  implementation("androidx.compose.material:material-icons-extended:1.4.3")

  //imageloader
  implementation("io.coil-kt:coil-compose:2.4.0")

  implementation(project(":payments:base:payment-manager"))
}
