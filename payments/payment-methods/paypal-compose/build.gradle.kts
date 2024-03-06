plugins {
  id("com.android.library")
  id("org.jetbrains.kotlin.android")
  id("kotlin-kapt")
  id("dagger.hilt.android.plugin")
}

apply("../../versions.gradle.kts")

android {
  val compileSdkVersion = getVersionFor("compileSdkVersion", "defaultCompileSdkVersion") as Int
  val minSdkVersion = getVersionFor("minSdkVersion", "defaultMinSdkVersion") as Int

  namespace = "com.appcoins.payment_method.paypal.presentation"
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

  lint {
    abortOnError = false
  }

  buildFeatures {
    // Enables Jetpack Compose for this module
    compose = true
    buildConfig = true
  }

  composeOptions {
    kotlinCompilerExtensionVersion = getVersionFor(
      "kotlinCompilerExtensionVersion", "defaultKotlinCompilerExtensionVersion"
    ).toString()
  }
}

fun getVersionFor(
  versionName: String,
  defaultVersionName: String,
) =
  runCatching { rootProject.extra[versionName] }.getOrDefault(project.extra[defaultVersionName])

dependencies {
  val hiltAndroidVersion = getVersionFor("hiltAndroidVersion", "defaultHiltAndroidVersion")
  val daggerHiltCompilerVersion =
    getVersionFor("daggerHiltCompilerVersion", "defaultDaggerHiltCompilerVersion")
  val hiltNavigationComposeVersion =
    getVersionFor("hiltNavigationComposeVersion", "defaultHiltNavigationComposeVersion")

  //Hilt
  implementation("com.google.dagger:hilt-android:$hiltAndroidVersion")
  kapt("com.google.dagger:hilt-compiler:$daggerHiltCompilerVersion")

  implementation("androidx.hilt:hilt-navigation-compose:$hiltNavigationComposeVersion")

  implementation(project(":payments:payment-methods:paypal"))
  api(project(":payments:payment-methods:paypal:magnes-aar"))
  api(project(":payments:base:payment-manager"))
  implementation(project(":payments:base:network"))
}
