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

  namespace = "com.appcoins.oem_extractor"
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
  val apacheCommonsTextVersion =
    getVersionFor("apacheCommonsTextVersion", "defaultApacheCommonsTextVersion")

  // lib only required for release because its missing in the extractor-jar
  releaseImplementation("org.apache.commons:commons-text:$apacheCommonsTextVersion")

  //Hilt
  implementation("com.google.dagger:hilt-android:$hiltAndroidVersion")
  kapt("com.google.dagger:hilt-compiler:$daggerHiltCompilerVersion")

  api(project(":payments:base:oem-extractor:extractor-jar"))
}
