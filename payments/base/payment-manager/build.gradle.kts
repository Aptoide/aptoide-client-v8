plugins {
  id("com.android.library")
  id("org.jetbrains.kotlin.android")
  id("com.google.devtools.ksp")
}

apply("../../versions.gradle.kts")

android {
  val compileSdkVersion = getVersionFor("compileSdkVersion").toInt()
  val minSdkVersion = getVersionFor("minSdkVersion").toInt()

  namespace = "com.appcoins.payment_manager"
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
  api(project(":payments:base:arch"))
  api(project(":payments:product-inventory"))
  api(project(":payments:base:network"))
  implementation(project(":payments:base:json"))
  ksp(project(":payments:base:json-ksp"))

  implementation(project(":payments:payment-methods:adyen"))
  implementation(project(":payments:payment-methods:paypal"))

  // Activity
  val androidxActivityVersion = getVersionFor("androidxActivityVersion")
  implementation("androidx.activity:activity-ktx:$androidxActivityVersion")
}
