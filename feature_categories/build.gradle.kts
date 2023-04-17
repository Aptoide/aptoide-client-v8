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
  namespace = "cm.aptoide.pt.feature_categories"
}

dependencies {
  implementation(project(ModuleDependency.APTOIDE_NETWORK))
  implementation(project(ModuleDependency.FEATURE_APPS))

  implementation(LibraryDependency.KOTLIN)

  //compose
  implementation(LibraryDependency.VIEWMODEL_COMPOSE)
  implementation(LibraryDependency.UI_TOOLING_COMPOSE)

  //network
  implementation(LibraryDependency.RETROFIT)
  implementation(LibraryDependency.RETROFIT_GSON_CONVERTER)

  //di
  implementation(LibraryDependency.HILT)
  implementation(LibraryDependency.HILT_NAV_COMPOSE)
  kapt(LibraryDependency.HILT_COMPILER)

  //logger
  implementation(LibraryDependency.TIMBER)
}