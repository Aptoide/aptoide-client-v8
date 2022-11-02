import BuildType.Companion.DEBUG
import BuildType.Companion.RELEASE

plugins {
  id(GradlePluginId.ANDROID_LIBRARY)
  id(GradlePluginId.KOTLIN_ANDROID)
  id(GradlePluginId.KOTLIN_ANDROID_EXTENSIONS)
  id(GradlePluginId.KOTLIN_KAPT)
  id(GradlePluginId.HILT_PLUGIN)
}

android {
  compileSdk = AndroidConfig.COMPILE_SDK

  defaultConfig {
    buildToolsVersion = AndroidConfig.BUILD_TOOLS
    minSdk = AndroidConfig.MIN_SDK
    targetSdk = AndroidConfig.TARGET_SDK
    testInstrumentationRunner = AndroidConfig.TEST_INSTRUMENTATION_RUNNER
  }

  buildTypes {
    getByName(RELEASE) {
      isMinifyEnabled = BuildTypeRelease.isMinifyEnabled
      proguardFiles("proguard-android.txt", "proguard-rules.pro")
    }
    getByName(DEBUG) {
      isMinifyEnabled = BuildTypeDebug.isMinifyEnabled
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }
  kotlinOptions {
    jvmTarget = JavaVersion.VERSION_1_8.toString()
  }
  namespace = "cm.aptoide.pt.aptoide_network"
}

dependencies {

  //di
  implementation(LibraryDependency.HILT)
  kapt(LibraryDependency.HILT_COMPILER)

  implementation(LibraryDependency.KOTLIN)

  //network
  implementation(LibraryDependency.RETROFIT)
  implementation(LibraryDependency.OK_HTTP)
  implementation(LibraryDependency.RETROFIT_GSON_CONVERTER)
  implementation(LibraryDependency.LOGGING_INTERCEPTOR)
}