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
  implementation(project(ModuleDependency.UTILS))


  implementation(LibraryDependency.KOTLIN)
  /*implementation(LibraryDependency.COROUTINES)
  testImplementation(TestLibraryDependency.COROUTINES_TEST)*/
  implementation(LibraryDependency.RXJAVA_2)
  api(LibraryDependency.ROOM_RXJAVA2)


  //di
  implementation(LibraryDependency.HILT)
  implementation(LibraryDependency.HILT_NAV_COMPOSE)
  kapt(LibraryDependency.HILT_COMPILER)

  //room
  implementation(LibraryDependency.ROOM)
  kapt(LibraryDependency.ROOM_COMPILER)
  implementation(LibraryDependency.ROOM_KTX)
  androidTestImplementation(TestLibraryDependency.ROOM_TESTING)

  //logger
  implementation(LibraryDependency.TIMBER)

  implementation(LibraryDependency.GSON)
}