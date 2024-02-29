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
  api(project(":payments:base:oem-extractor:extractor-jar"))
  implementation(LibraryDependency.APACHE_COMMONS_TEXT)
}
