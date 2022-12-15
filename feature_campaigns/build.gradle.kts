plugins {
  id(GradlePluginId.ANDROID_LIBRARY)
  id(GradlePluginId.KOTLIN_ANDROID)
  id(GradlePluginId.JUNIT5_PLUGIN)
  id(GradlePluginId.KOTLIN_KAPT)
  id(GradlePluginId.HILT_PLUGIN)
}

android {
  namespace = "cm.aptoide.pt.feature_campaigns"
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
  implementation(project(ModuleDependency.APTOIDE_NETWORK))

  implementation(LibraryDependency.OK_HTTP)
  implementation(LibraryDependency.HILT)
  implementation(LibraryDependency.GSON)
  implementation(LibraryDependency.RETROFIT)
  implementation(LibraryDependency.RETROFIT_GSON_CONVERTER)
  implementation(LibraryDependency.GMS_PLAY_SERVICES_ADS)
  kapt(LibraryDependency.HILT_COMPILER)

  implementation(LibraryDependency.CORE_KTX)
  implementation(LibraryDependency.COROUTINES)
  implementation(LibraryDependency.KOTLIN)

  testRuntimeOnly(TestLibraryDependency.JUNIT_JUPITER_ENGINE)
  testImplementation(project(ModuleDependency.UTIL))
}