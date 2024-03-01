plugins {
  id("com.android.library")
  id("org.jetbrains.kotlin.android")
}

apply("../versions.gradle.kts")

android {
  val compileSdkVersion = getVersionFor("compileSdkVersion").toInt()
  val minSdkVersion = getVersionFor("minSdkVersion").toInt()

  compileSdk = compileSdkVersion
  namespace = "com.appcoins.billing.sdk"

  buildFeatures {
    aidl = true
    buildConfig = true
  }

  defaultConfig {
    minSdk = minSdkVersion

    val supportedSdkVersion = getVersionFor("supportedSdkVersion")
    buildConfigField("int", "SUPPORTED_API_VERSION", "$supportedSdkVersion")

    consumerProguardFiles("consumer-rules.pro")

    val sdkState = (System.getenv("DEFAULT_IAB_STATE") ?: true).toString()
    manifestPlaceholders["defaultSDKState"] = sdkState
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
  val kotlinxCoroutinesAndroidVersion = getVersionFor("kotlinxCoroutinesAndroidVersion")

  implementation(project(":payments:base:network"))
  api(project(":payments:guest-wallet"))
  implementation(project(":payments:product-inventory"))

  implementation("com.google.code.gson:gson:2.10.1")

  // coroutines
  implementation(
    "org.jetbrains.kotlinx:kotlinx-coroutines-android:$kotlinxCoroutinesAndroidVersion"
  )
}
