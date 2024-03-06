plugins {
  id("com.android.library")
  id("org.jetbrains.kotlin.android")
}

apply("../../versions.gradle.kts")

android {
  val compileSdkVersion = getVersionFor("compileSdkVersion", "defaultCompileSdkVersion") as Int
  val minSdkVersion = getVersionFor("minSdkVersion", "defaultMinSdkVersion") as Int

  namespace = "com.appcoins.payments.arch"
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
}

fun getVersionFor(
  versionName: String,
  defaultVersionName: String,
) =
  runCatching { rootProject.extra[versionName] }.getOrDefault(project.extra[defaultVersionName])

dependencies {
  val coreKtsVersion = getVersionFor("coreKtsVersion", "defaultCoreKtsVersion")
  val kotlinxCoroutinesAndroidVersion =
    getVersionFor("kotlinxCoroutinesAndroidVersion", "defaultKotlinxCoroutinesAndroidVersion")

  implementation("androidx.core:core-ktx:$coreKtsVersion")

  // coroutines
  implementation(
    "org.jetbrains.kotlinx:kotlinx-coroutines-android:$kotlinxCoroutinesAndroidVersion"
  )
}
