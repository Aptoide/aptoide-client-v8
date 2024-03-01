plugins {
  id("com.android.library")
  id("org.jetbrains.kotlin.android")
  id("kotlin-kapt")
  id("dagger.hilt.android.plugin")
}

apply("../../versions.gradle.kts")

android {
  val compileSdkVersion = getVersionFor("compileSdkVersion").toInt()
  val minSdkVersion = getVersionFor("minSdkVersion").toInt()

  namespace = "com.appcoins.payment_method.adyen.presentation"
  compileSdk = compileSdkVersion

  defaultConfig {
    minSdk = minSdkVersion

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

  buildFeatures {
    // Enables Jetpack Compose for this module
    compose = true
    buildConfig = true
  }

  composeOptions {
    kotlinCompilerExtensionVersion = getVersionFor("kotlinCompilerExtensionVersion")
  }
}

fun getVersionFor(versionName: String) =
  runCatching { rootProject.extra[versionName] }
    .getOrDefault(project.extra[versionName])
    .toString()

dependencies {
  val navigationComposeVersion = getVersionFor("navigationComposeVersion")
  val hiltNavigationComposeVersion = getVersionFor("hiltNavigationComposeVersion")
  val composeMaterialVersion = getVersionFor("composeMaterialVersion")
  val lifecycleViewModelComposeVersion = getVersionFor("lifecycleViewModelComposeVersion")
  val hiltAndroidVersion = getVersionFor("hiltAndroidVersion")
  val daggerHiltCompilerVersion = getVersionFor("daggerHiltCompilerVersion")
  val adyenVersion = getVersionFor("adyenVersion")

  //Hilt
  implementation("com.google.dagger:hilt-android:$hiltAndroidVersion")
  kapt("com.google.dagger:hilt-compiler:$daggerHiltCompilerVersion")

  implementation("androidx.navigation:navigation-compose:$navigationComposeVersion")
  implementation("androidx.hilt:hilt-navigation-compose:$hiltNavigationComposeVersion")

  implementation("androidx.compose.material:material:$composeMaterialVersion")
  implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycleViewModelComposeVersion")

  implementation(project(":payments:base:arch"))
  implementation(project(":payments:payment-methods:adyen"))
  implementation(project(":payments:base:payment-manager"))


  api("com.adyen.checkout:card:$adyenVersion")
  api("com.adyen.checkout:3ds2:$adyenVersion")
  api("com.adyen.checkout:redirect:$adyenVersion")
}
