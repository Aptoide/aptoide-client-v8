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

  namespace = "com.appcoins.payment_manager.presentation"
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
  val coreKtsVersion = getVersionFor("coreKtsVersion", "defaultCoreKtsVersion")
  val kotlinStdlibJdkVersion = getVersionFor("kotlinStdlibJdkVersion","defaultKotlinStdlibJdkVersion")
  val kotlinxCoroutinesAndroidVersion = getVersionFor("kotlinxCoroutinesAndroidVersion","defaultKotlinxCoroutinesAndroidVersion")
  val kotlinxCoroutinesCoreVersion = getVersionFor("kotlinxCoroutinesCoreVersion","defaultKotlinxCoroutinesCoreVersion")
  val timberVersion = getVersionFor("timberVersion","defaultTimberVersion")

  implementation("androidx.core:core-ktx:$coreKtsVersion")
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlinStdlibJdkVersion")

  // coroutines
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$kotlinxCoroutinesAndroidVersion")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesCoreVersion")

  //logger
  implementation("com.jakewharton.timber:timber:$timberVersion")

  //Hilt
  val hiltAndroidVersion = getVersionFor("hiltAndroidVersion","defaultHiltAndroidVersion")
  val daggerHiltCompilerVersion = getVersionFor("daggerHiltCompilerVersion","defaultDaggerHiltCompilerVersion")
  val androidxHiltCompilerVersion = getVersionFor("androidxHiltCompilerVersion","defaultAndroidxHiltCompilerVersion")

  implementation("com.google.dagger:hilt-android:$hiltAndroidVersion")
  kapt("com.google.dagger:hilt-compiler:$daggerHiltCompilerVersion")
  kapt("androidx.hilt:hilt-compiler:$androidxHiltCompilerVersion")

  val appCompatVersion = getVersionFor("appCompatVersion","defaultAppCompatVersion")
  val activityKtxVersion = getVersionFor("activityKtxVersion","defaultActivityKtxVersion")
  val androidMaterialVersion = getVersionFor("androidMaterialVersion","defaultAndroidMaterialVersion")
  val navigationComposeVersion = getVersionFor("navigationComposeVersion","defaultNavigationComposeVersion")
  val hiltNavigationComposeVersion = getVersionFor("hiltNavigationComposeVersion","defaultHiltNavigationComposeVersion")
  val composeMaterialVersion = getVersionFor("composeMaterialVersion","defaultComposeMaterialVersion")
  val composeAnimationVersion = getVersionFor("composeAnimationVersion","defaultComposeAnimationVersion")
  val composeUiToolingVersion = getVersionFor("composeUiToolingVersion","defaultComposeUiToolingVersion")
  val lifecycleViewModelComposeVersion = getVersionFor("lifecycleViewModelComposeVersion","defaultLifecycleViewModelComposeVersion")
  val materialIconsExtendedVersion = getVersionFor("materialIconsExtendedVersion","defaultMaterialIconsExtendedVersion")
  val coilComposeVersion = getVersionFor("coilComposeVersion","defaultCoilComposeVersion")

  implementation("androidx.appcompat:appcompat:$appCompatVersion")
  implementation("androidx.activity:activity-ktx:$activityKtxVersion")
  implementation("com.google.android.material:material:$androidMaterialVersion")

  implementation("androidx.navigation:navigation-compose:$navigationComposeVersion")
  implementation("androidx.hilt:hilt-navigation-compose:$hiltNavigationComposeVersion")

  implementation("androidx.compose.material:material:$composeMaterialVersion")
  implementation("androidx.compose.animation:animation:$composeAnimationVersion")
  implementation("androidx.compose.ui:ui-tooling:$composeUiToolingVersion")
  implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycleViewModelComposeVersion")
  implementation("androidx.compose.material:material-icons-extended:$materialIconsExtendedVersion")

  //imageloader
  implementation("io.coil-kt:coil-compose:$coilComposeVersion")

  implementation(project(":payments:base:payment-manager"))
}
