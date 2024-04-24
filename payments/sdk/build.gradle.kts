plugins {
  id("com.android.library")
  id("org.jetbrains.kotlin.android")
  id("com.google.devtools.ksp")
}

apply("../versions.gradle.kts")

android {
  val compileSdkVersion = getVersionFor("compileSdkVersion").toInt()
  val minSdkVersion = getVersionFor("minSdkVersion").toInt()

  compileSdk = compileSdkVersion
  namespace = "com.appcoins.payments.sdk"

  buildFeatures {
    aidl = true
    buildConfig = true
  }

  defaultConfig {
    minSdk = minSdkVersion

    val supportedSdkVersion = getVersionFor("supportedSdkVersion")
    buildConfigField("int", "SUPPORTED_API_VERSION", supportedSdkVersion)

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
  api(project(":payments:arch"))
  implementation(project(":payments:products"))
  implementation(project(":payments:json"))
  ksp(project(":payments:json:ksp"))

  // Coroutines
  val kotlinxCoroutinesAndroidVersion = getVersionFor("kotlinxCoroutinesAndroidVersion")
  implementation(
    "org.jetbrains.kotlinx:kotlinx-coroutines-android:$kotlinxCoroutinesAndroidVersion"
  )
}
