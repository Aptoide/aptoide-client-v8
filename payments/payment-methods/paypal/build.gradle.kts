plugins {
  id("com.android.library")
  id("org.jetbrains.kotlin.android")
}

apply("../../versions.gradle.kts")

android {
  val compileSdkVersion = getVersionFor("compileSdkVersion").toInt()
  val minSdkVersion = getVersionFor("minSdkVersion").toInt()

  namespace = "com.appcoins.payment_method.paypal"
  compileSdk = compileSdkVersion

  buildFeatures {
    buildConfig = true
  }

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
  api(project(":payments:payment-methods:paypal:magnes-aar"))
  implementation(project(":payments:base:arch"))
  implementation(project(":payments:base:network"))
  
  implementation("androidx.activity:activity-ktx:1.7.2")
}
