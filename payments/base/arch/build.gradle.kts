plugins {
  id("com.android.library")
  id("org.jetbrains.kotlin.android")
}

android {
  namespace = "com.appcoins.payments.arch"
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
}
