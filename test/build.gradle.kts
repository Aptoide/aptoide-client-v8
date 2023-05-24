plugins {
  id(GradlePluginId.ANDROID_LIBRARY)
  id(GradlePluginId.KOTLIN_ANDROID)
  id(GradlePluginId.JUNIT5_PLUGIN)
}

android {
  namespace = "cm.aptoide.pt.test"
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
    sourceCompatibility = JavaLibrary.SOURCE_COMPATIBILITY_JAVA_VERSION
    targetCompatibility = JavaLibrary.TARGET_COMPATIBILITY_JAVA_VERSION
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