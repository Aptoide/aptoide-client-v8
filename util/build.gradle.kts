plugins {
  id(GradlePluginId.ANDROID_LIBRARY)
  id(GradlePluginId.KOTLIN_ANDROID)
  id(GradlePluginId.JUNIT5_PLUGIN)
}

android {
  namespace = "cm.aptoide.pt.util"
  compileSdk = AndroidConfig.COMPILE_SDK

  defaultConfig {
    minSdk = AndroidConfig.MIN_SDK
    targetSdk = AndroidConfig.TARGET_SDK
  }

  buildTypes {
    getByName(BuildType.RELEASE) {
      isMinifyEnabled = BuildTypeRelease.isMinifyEnabled
      proguardFiles("proguard-android.txt", "proguard-rules.pro")
    }
    getByName(BuildType.DEBUG) {
      isMinifyEnabled = BuildTypeDebug.isMinifyEnabled
    }
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }
  kotlinOptions {
    jvmTarget = "1.8"
  }
}

dependencies {

  api(TestLibraryDependency.JUNIT)

  // New TDD dependencies
  api(TestLibraryDependency.JUNIT_JUPITER_API)
  testRuntimeOnly(TestLibraryDependency.JUNIT_JUPITER_ENGINE)
  api(TestLibraryDependency.JUNIT_JUPITER_PARAMS)
  api(TestLibraryDependency.JUNIT_JUPITER_VANTAGE)
  api(TestLibraryDependency.COROUTINES_TEST)
  api(TestLibraryDependency.TURBINE)
}