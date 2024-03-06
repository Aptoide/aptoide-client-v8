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

  lint {
    abortOnError = false
  }

  buildFeatures {
    // Enables Jetpack Compose for this module
    compose = true
    buildConfig = true
  }

  composeOptions {
    kotlinCompilerExtensionVersion = getVersionFor("kotlinCompilerExtensionVersion", "defaultKotlinCompilerExtensionVersion").toString()
  }
}

fun getVersionFor(versionName: String, defaultVersionName: String) =
  runCatching { rootProject.extra[versionName] }.getOrDefault(project.extra[defaultVersionName])

dependencies {
  val navigationComposeVersion = getVersionFor("navigationComposeVersion","defaultNavigationComposeVersion")
  val hiltNavigationComposeVersion = getVersionFor("hiltNavigationComposeVersion","defaultHiltNavigationComposeVersion")
  val composeMaterialVersion = getVersionFor("composeMaterialVersion","defaultComposeMaterialVersion")
  val lifecycleViewModelComposeVersion = getVersionFor("lifecycleViewModelComposeVersion","defaultLifecycleViewModelComposeVersion")

  val hiltAndroidVersion = getVersionFor("hiltAndroidVersion","defaultHiltAndroidVersion")
  val daggerHiltCompilerVersion = getVersionFor("daggerHiltCompilerVersion","defaultDaggerHiltCompilerVersion")

  //Hilt
  implementation("com.google.dagger:hilt-android:$hiltAndroidVersion")
  kapt("com.google.dagger:hilt-compiler:$daggerHiltCompilerVersion")

  implementation("androidx.navigation:navigation-compose:$navigationComposeVersion")
  implementation("androidx.hilt:hilt-navigation-compose:$hiltNavigationComposeVersion")

  implementation("androidx.compose.material:material:$composeMaterialVersion")
  implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycleViewModelComposeVersion")

  implementation(project(":payments:payment-methods:adyen"))

  val adyenVersion = getVersionFor("adyenVersion","defaultAdyenVersion")

  api("com.adyen.checkout:card:$adyenVersion")
  api("com.adyen.checkout:3ds2:$adyenVersion")
  api("com.adyen.checkout:redirect:$adyenVersion")
}
