plugins {
  id(GradlePluginId.ANDROID_LIBRARY)
  id(GradlePluginId.ANDROID_MODULE)
  id(GradlePluginId.COMPOSABLE)
  id(GradlePluginId.HILT)
}

android {
  namespace = "cm.aptoide.pt.oem_extractor"
}

dependencies {
  api(files("libs/extractor.jar"))
  implementation(project(ModuleDependency.EXTENSIONS))
  implementation(LibraryDependency.APACHE_COMMONS_TEXT)
}
