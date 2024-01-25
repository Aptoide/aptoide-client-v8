plugins {
  id(GradlePluginId.ANDROID_LIBRARY)
  id(GradlePluginId.ANDROID_MODULE)
  id(GradlePluginId.COMPOSABLE)
  id(GradlePluginId.HILT)
}

android {
  namespace = "com.appcoins.oem_extractor"
}

dependencies {
  api(files("libs/extractor.jar"))
  implementation(LibraryDependency.APACHE_COMMONS_TEXT)
}
