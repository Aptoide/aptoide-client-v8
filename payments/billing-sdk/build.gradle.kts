plugins {
  id("com.android.library")
  id("org.jetbrains.kotlin.android")
  id("kotlin-kapt")
  id("dagger.hilt.android.plugin")
}

android {
  namespace = "com.appcoins.billing.sdk"

  buildFeatures {
    aidl = true
    buildConfig = true
  }

  defaultConfig {
    buildConfigField("int", "SUPPORTED_API_VERSION", "3")
  }
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
}

dependencies {
  implementation("androidx.core:core-ktx:1.10.1")
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.8.22")

  // coroutines
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")

  //logger
  implementation("com.jakewharton.timber:timber:5.0.1")

  //hilt
  implementation("com.google.dagger:hilt-android:2.46.1")
  kapt("com.google.dagger:hilt-compiler:2.46.1")
  kapt("androidx.hilt:hilt-compiler:1.0.0")

  api(project(":payments:guest-wallet"))
  implementation(project(":payments:product-inventory"))

  implementation("com.google.code.gson:gson:2.10.1")
  implementation("io.catappult:communication:0.6.8.0")
}
