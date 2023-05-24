plugins {
  id(GradlePluginId.ANDROID_LIBRARY)
  id(GradlePluginId.KOTLIN_ANDROID)
  id(GradlePluginId.JUNIT5_PLUGIN)
  id(GradlePluginId.KOTLIN_KAPT)
}

android {
  namespace = "cm.aptoide.pt.install_manager"
  compileSdk = AndroidConfig.COMPILE_SDK

  defaultConfig {
    buildToolsVersion = AndroidConfig.BUILD_TOOLS
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

  implementation(LibraryDependency.CORE_KTX)
  implementation(LibraryDependency.COROUTINES)
  implementation(LibraryDependency.KOTLIN)

  testRuntimeOnly(TestLibraryDependency.JUNIT_JUPITER_ENGINE)
  testImplementation(project(ModuleDependency.TEST))
}
