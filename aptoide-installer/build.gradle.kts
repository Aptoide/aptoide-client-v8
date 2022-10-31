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
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
  implementation(project(ModuleDependency.DOWNLOAD_MANAGER))
  implementation(project(ModuleDependency.DOWNLOADS_DATABASE))
  implementation(project(ModuleDependency.UTILS))
  implementation(project(ModuleDependency.INSTALLED_APPS))
  implementation(project(ModuleDependency.PACKAGE_INSTALLER))
  implementation(project(ModuleDependency.FEATURE_APPS))


  api(LibraryDependency.FILE_DOWNLOADER)
  implementation(LibraryDependency.FILE_DOWNLOADER_OKHTTP3)

  implementation(LibraryDependency.COROUTINES_CORE)
  implementation(LibraryDependency.COROUTINES_RXJAVA_2)
  implementation(LibraryDependency.RXJAVA_2)

  //di
  implementation(LibraryDependency.HILT)
  implementation(LibraryDependency.HILT_NAV_COMPOSE)
  kapt(LibraryDependency.HILT_COMPILER)

}