plugins {
  id(GradlePluginId.ANDROID_LIBRARY)
  id(GradlePluginId.ANDROID_MODULE)
  id(GradlePluginId.COMPOSABLE)
  id(GradlePluginId.HILT)
}

android {
  namespace = "cm.aptoide.pt.settings"
}

dependencies {
  implementation(project(ModuleDependency.APTOIDE_UI))
  implementation(project(ModuleDependency.ENVIRONMENT_INFO))

  //store
  implementation(LibraryDependency.DATASTORE)
}