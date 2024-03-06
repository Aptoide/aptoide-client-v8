plugins {
  id("com.android.library")
  id("org.jetbrains.kotlin.android")
  id("kotlin-kapt")
  id("dagger.hilt.android.plugin")
}

apply("../versions.gradle.kts")

android {
  val compileSdkVersion = getVersionFor("compileSdkVersion", "defaultCompileSdkVersion") as Int
  val minSdkVersion = getVersionFor("minSdkVersion", "defaultMinSdkVersion") as Int

  namespace = "com.appcoins.uri_handler"
  compileSdk = compileSdkVersion

  defaultConfig {
    minSdk = minSdkVersion
    val iabState = (System.getenv("DEFAULT_IAB_STATE") ?: true).toString()

    manifestPlaceholders += mapOf(
      "payment_intent_filter_priority" to "\${payment_intent_filter_priority}",
      "payment_intent_filter_host" to "\${payment_intent_filter_host}",
      "applicationId" to "\${applicationId}",
      "adyenCheckoutScheme" to "\${adyenCheckoutScheme}",
      "defaultIABState" to iabState,
    )

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
  val hiltAndroidVersion = getVersionFor("hiltAndroidVersion", "defaultHiltAndroidVersion")
  val daggerHiltCompilerVersion =
    getVersionFor("daggerHiltCompilerVersion", "defaultDaggerHiltCompilerVersion")
  val androidMaterialVersion =
    getVersionFor("androidMaterialVersion", "defaultAndroidMaterialVersion")

  //Hilt
  implementation("com.google.dagger:hilt-android:$hiltAndroidVersion")
  kapt("com.google.dagger:hilt-compiler:$daggerHiltCompilerVersion")

  api(project(":payments:base:payment-manager"))
  implementation(project(":payments:base:oem-extractor"))

  // Material
  implementation("com.google.android.material:material:$androidMaterialVersion")
}
