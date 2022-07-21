plugins {
  id("com.android.library")
  id("org.jetbrains.kotlin.android")
}

android {
  compileSdk = 32

  defaultConfig {
    minSdk = 21
    targetSdk = 32

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    consumerProguardFiles("consumer-rules.pro")
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


  api("com.liulishuo.filedownloader:library:${LibraryVersionOldModules.FILE_DOWNLOADER}")
  implementation("cn.dreamtobe.filedownloader:filedownloader-okhttp3-connection:${LibraryVersionOldModules.FILE_DOWNLOADER_OK_HTTP}")

  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${LibraryVersionOldModules.COROUTINES}")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-rx2:${LibraryVersionOldModules.COROUTINES}")
  implementation(LibraryDependency.RXJAVA_2)
  implementation(project(mapOf("path" to ":packageinstaller")))
}