plugins {
  id("com.android.library")
  id("org.jetbrains.kotlin.android")
}

apply("../versions.gradle.kts")

android {
  val compileSdkVersion = getVersionFor("compileSdkVersion").toInt()
  val minSdkVersion = getVersionFor("minSdkVersion").toInt()

  namespace = "com.appcoins.payments.network"
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
}

fun getVersionFor(versionName: String) =
  runCatching { rootProject.extra[versionName] }
    .getOrDefault(project.extra[versionName])
    .toString()

dependencies {
  implementation(project(":payments:arch"))

  // Coroutines
  val kotlinxCoroutinesAndroidVersion = getVersionFor("kotlinxCoroutinesAndroidVersion")
  implementation(
    "org.jetbrains.kotlinx:kotlinx-coroutines-android:$kotlinxCoroutinesAndroidVersion"
  )
}