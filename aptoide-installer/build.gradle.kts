plugins {
  id(GradlePluginId.ANDROID_LIBRARY)
  id(GradlePluginId.KOTLIN_ANDROID)
  id(GradlePluginId.KOTLIN_KAPT)
  id(GradlePluginId.HILT_PLUGIN)
  id(GradlePluginId.JUNIT5_PLUGIN)
}

android {
  compileSdk = AndroidConfig.COMPILE_SDK

  defaultConfig {
    minSdk = AndroidConfig.MIN_SDK
    targetSdk = AndroidConfig.TARGET_SDK

    testInstrumentationRunner = AndroidConfig.TEST_INSTRUMENTATION_RUNNER
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }

  compileOptions {
    sourceCompatibility = JavaLibrary.SOURCE_COMPATIBILITY_JAVA_VERSION
    targetCompatibility = JavaLibrary.TARGET_COMPATIBILITY_JAVA_VERSION
  }
  namespace = "cm.aptoide.pt.installer"
}

dependencies {
  implementation(project(ModuleDependency.INSTALL_MANAGER))
  implementation(project(ModuleDependency.APTOIDE_NETWORK))

  implementation(LibraryDependency.APP_COMPAT)

  //lifecycle
  implementation(LibraryDependency.LIFECYCLE_COMMON)
  implementation(LibraryDependency.LIFECYCLE_PROCESS)

  // Network
  implementation(LibraryDependency.OK_HTTP)

  // Kotlin
  implementation(LibraryDependency.KOTLIN)
  implementation(LibraryDependency.COROUTINES)

  //di
  implementation(LibraryDependency.HILT)
  kapt(LibraryDependency.HILT_COMPILER)

  testRuntimeOnly(TestLibraryDependency.JUNIT_JUPITER_ENGINE)
  testImplementation(project(ModuleDependency.TEST))
}
