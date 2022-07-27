plugins {
  id(GradlePluginId.ANDROID_LIBRARY)
  id(GradlePluginId.KOTLIN_ANDROID)

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
}

dependencies {
  implementation(project(ModuleDependency.DOWNLOADS_DATABASE))
  implementation(project(ModuleDependency.UTILS))

  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${LibraryVersionOldModules.COROUTINES}")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-rx2:${LibraryVersionOldModules.COROUTINES}")
  implementation(LibraryDependency.RXJAVA_2)

  // Testing-only dependencies
  testImplementation(TestLibraryDependency.JUNIT)
  testImplementation(TestLibraryDependency.MOCKITO_CORE)
}
