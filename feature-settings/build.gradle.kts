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

  flavorDimensions.add(0, "product")
  productFlavors {
    register("vanilla") {
      dimension = "product"
    }
    register("cobrand") {
      dimension = "product"
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
  implementation(project(ModuleDependency.APTOIDE_UI))

  implementation(LibraryDependency.CORE_KTX)
  implementation(LibraryDependency.APP_COMPAT)
  implementation(LibraryDependency.MATERIAL)
  implementation(LibraryDependency.KOTLIN)
  testImplementation(TestLibraryDependency.JUNIT)
  androidTestImplementation(TestLibraryDependency.JUNIT_ANDROIDX)

  //animations
  implementation(LibraryDependency.LOTTIE)

  //network
  implementation(LibraryDependency.RETROFIT)
  implementation(LibraryDependency.RETROFIT_GSON_CONVERTER)
  implementation(LibraryDependency.OK_HTTP)
  implementation(LibraryDependency.LOGGING_INTERCEPTOR)

  //compose-ui
  implementation(LibraryDependency.ACTIVITY_COMPOSE)
  implementation(LibraryDependency.MATERIAL_COMPOSE)
  implementation(LibraryDependency.ANIMATION_COMPOSE)
  implementation(LibraryDependency.UI_TOOLING_COMPOSE)
  implementation(LibraryDependency.VIEWMODEL_COMPOSE)
  implementation(LibraryDependency.NAVIGATION_COMPOSE)

  //image-loader
  implementation(LibraryDependency.COIL)
  implementation(LibraryDependency.COIL_COMPOSE)

  //di
  implementation(LibraryDependency.HILT)
  implementation(LibraryDependency.HILT_NAV_COMPOSE)
  kapt(LibraryDependency.HILT_COMPILER)

  //logger
  implementation(LibraryDependency.TIMBER)

  //settings
  implementation("com.github.JamalMulla:ComposePrefs:1.0.5") // probably needs replacing, lacking some customization options.
  implementation(LibraryDependency.DATASTORE)

  //webview
  implementation(LibraryDependency.CUSTOM_CHROME_TAB)


}