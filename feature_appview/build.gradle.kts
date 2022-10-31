plugins {
  id(GradlePluginId.ANDROID_LIBRARY)
  id(GradlePluginId.KOTLIN_ANDROID)
  id(GradlePluginId.KOTLIN_KAPT)
  id(GradlePluginId.HILT_PLUGIN)
}

android {
  compileSdk = AndroidConfig.COMPILE_SDK

  defaultConfig {
    minSdk = AndroidConfig.MIN_SDK
    targetSdk = AndroidConfig.TARGET_SDK

    testInstrumentationRunner = AndroidConfig.TEST_INSTRUMENTATION_RUNNER
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

  buildFeatures {
    // Enables Jetpack Compose for this module
    compose = true
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }
  kotlinOptions {
    jvmTarget = JavaVersion.VERSION_1_8.toString()
  }
  composeOptions {
    kotlinCompilerExtensionVersion = CoreVersion.KT_COMPILER_EXTENSION
  }
}

dependencies {

  implementation(project(ModuleDependency.APTOIDE_NETWORK))
  implementation(project(ModuleDependency.FEATURE_APPS))
  api(project(ModuleDependency.FEATURE_REPORT_APP))
  implementation(project(ModuleDependency.APTOIDE_UI))
  implementation(project(ModuleDependency.FEATURE_EDITORIAL))
  implementation(project(ModuleDependency.DOWNLOAD_VIEW))
  implementation(project(ModuleDependency.FEATURE_REACTIONS))
  implementation(project(ModuleDependency.FEATURE_CAMPAIGNS))

  implementation(LibraryDependency.CORE_KTX)
  implementation(LibraryDependency.APP_COMPAT)
  implementation(LibraryDependency.MATERIAL)
  implementation(LibraryDependency.CONSTRAINT_LAYOUT)
  implementation(LibraryDependency.KOTLIN)
  implementation(LibraryDependency.RETROFIT)
  implementation(LibraryDependency.RETROFIT_GSON_CONVERTER)
  implementation(LibraryDependency.OK_HTTP)
  implementation(LibraryDependency.LOGGING_INTERCEPTOR)
  implementation(LibraryDependency.COROUTINES)
  testImplementation(TestLibraryDependency.COROUTINES_TEST)

  //compose
  implementation(LibraryDependency.MATERIAL_COMPOSE)
  implementation(LibraryDependency.ANIMATION_COMPOSE)
  implementation(LibraryDependency.UI_TOOLING_COMPOSE)
  implementation(LibraryDependency.VIEWMODEL_COMPOSE)
  implementation(LibraryDependency.NAVIGATION_COMPOSE)

  //di
  implementation(LibraryDependency.HILT)
  implementation(LibraryDependency.HILT_NAV_COMPOSE)
  kapt(LibraryDependency.HILT_COMPILER)

  //room
  implementation(LibraryDependency.ROOM)
  kapt(LibraryDependency.ROOM_COMPILER)
  implementation(LibraryDependency.ROOM_KTX)
  androidTestImplementation(TestLibraryDependency.ROOM_TESTING)

  //imageloader
  implementation(LibraryDependency.COIL_COMPOSE)

  //logger
  implementation(LibraryDependency.TIMBER)

  implementation(LibraryDependency.CUSTOM_CHROME_TAB)

  testImplementation(TestLibraryDependency.JUNIT)
  androidTestImplementation(TestLibraryDependency.JUNIT_ANDROIDX)
  androidTestImplementation(TestLibraryDependency.ESPRESSO_CORE)
}