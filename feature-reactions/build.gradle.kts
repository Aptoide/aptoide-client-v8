plugins {
  id(GradlePluginId.ANDROID_LIBRARY)
  id(GradlePluginId.KOTLIN_ANDROID)
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
    getByName(BuildType.RELEASE) {
      isMinifyEnabled = BuildTypeRelease.isMinifyEnabled
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }

    getByName(BuildType.DEBUG) {
      isMinifyEnabled = BuildTypeDebug.isMinifyEnabled
    }
  }

  buildFeatures {
    // Enables Jetpack Compose for this module
    compose = true
    buildConfig = true
  }

  compileOptions {
    sourceCompatibility = JavaLibrary.SOURCE_COMPATIBILITY_JAVA_VERSION
    targetCompatibility = JavaLibrary.TARGET_COMPATIBILITY_JAVA_VERSION
  }

  composeOptions {
    kotlinCompilerExtensionVersion = CoreVersion.KT_COMPILER_EXTENSION
  }
  namespace = "cm.aptoide.pt.feature_reactions"
}

dependencies {
  implementation(project(ModuleDependency.APTOIDE_NETWORK))
  implementation(project(ModuleDependency.APTOIDE_UI))

  implementation(LibraryDependency.CORE_KTX)
  implementation(LibraryDependency.APP_COMPAT)
  implementation(LibraryDependency.MATERIAL)
  implementation(LibraryDependency.KOTLIN)
  testImplementation(TestLibraryDependency.JUNIT)
  androidTestImplementation(TestLibraryDependency.JUNIT_ANDROIDX)

  //rxjava
  implementation(LibraryDependency.RXJAVA)

  //animations
  implementation(LibraryDependency.LOTTIE)

  //network
  implementation(LibraryDependency.RETROFIT)
  implementation(LibraryDependency.RETROFIT_GSON_CONVERTER)
  implementation(LibraryDependency.OK_HTTP)
  implementation(LibraryDependency.LOGGING_INTERCEPTOR)

  //image-loader
  implementation(LibraryDependency.COIL)

  //di
  implementation(LibraryDependency.HILT)
  implementation(LibraryDependency.HILT_NAV_COMPOSE)
  kapt(LibraryDependency.HILT_COMPILER)

  //logger
  implementation(LibraryDependency.TIMBER)

}