plugins {
  id(GradlePluginId.ANDROID_LIBRARY)
  id(GradlePluginId.ANDROID_MODULE)
  id(GradlePluginId.HILT)
}

android {
  namespace = "com.appcoins.oem_extractor"

  buildFeatures {
    buildConfig = true
  }
}

dependencies {
  api(files("libs/extractor.jar"))
  implementation(LibraryDependency.APACHE_COMMONS_TEXT)
}
