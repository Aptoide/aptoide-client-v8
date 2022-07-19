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
  implementation(project(ModuleDependency.APTOIDE_DATABASE))
  implementation(project(ModuleDependency.UTILS))

  //testImplementation(TestLibraryDependency.JUNIT)

  //noinspection GradleDependency
  //implementation "androidx.appcompat:appcompat:${LibraryVersionOldModules.APP_COMPAT}"

  api("com.liulishuo.filedownloader:library:${LibraryVersionOldModules.FILE_DOWNLOADER}")
  implementation("cn.dreamtobe.filedownloader:filedownloader-okhttp3-connection:${LibraryVersionOldModules.FILE_DOWNLOADER_OK_HTTP}")

  //implementation("io.reactivex:rxjava:${LibraryVersionOldModules.RXJAVA}")

  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${LibraryVersionOldModules.COROUTINES}")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-rx2:${LibraryVersionOldModules.COROUTINES}")
  implementation("io.reactivex.rxjava2:rxjava:${LibraryVersionOldModules.RXJAVA_2}")

  //implementation "nl.littlerobots.rxlint:rxlint:${LibraryVersionOldModules.RX_LINT}"
  // And ProGuard rules for RxJava!
  /*implementation "com.artemzin.rxjava:proguard-rules:${LibraryVersionOldModules.RXJAVA_PROGUARD_RULES}"
  implementation "com.squareup.retrofit2:retrofit:${LibraryVersionOldModules.RETROFIT}"*/

  // Testing-only dependencies
  testImplementation(TestLibraryDependency.JUNIT)
  testImplementation(TestLibraryDependency.MOCKITO_CORE)
}
