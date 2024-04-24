plugins {
  id("com.android.library")
  id("org.jetbrains.kotlin.android")
  id("com.google.devtools.ksp")
}

apply("../versions.gradle.kts")

android {
  val compileSdkVersion = getVersionFor("compileSdkVersion").toInt()
  val minSdkVersion = getVersionFor("minSdkVersion").toInt()

  namespace = "com.appcoins.payments.guest_wallet"

  buildFeatures {
    buildConfig = true
  }
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
  api(project(":payments:arch"))
  api(project(":payments:network"))
  implementation(project(":payments:json"))
  ksp(project(":payments:json:ksp"))
}
