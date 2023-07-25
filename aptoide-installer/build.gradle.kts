plugins {
  id(GradlePluginId.ANDROID_LIBRARY)
  id(GradlePluginId.ANDROID_MODULE)
  id(GradlePluginId.HILT)
  id(GradlePluginId.COMPOSABLE)
}

android {
  namespace = "cm.aptoide.pt.installer"
}

dependencies {
  implementation(project(ModuleDependency.INSTALL_MANAGER))
  implementation(project(ModuleDependency.APTOIDE_NETWORK))
  implementation(project(ModuleDependency.EXTENSIONS))

  implementation(LibraryDependency.ACTIVITY_KTX)
  implementation(LibraryDependency.APP_COMPAT)

  //lifecycle
  implementation(LibraryDependency.LIFECYCLE_COMMON)
  implementation(LibraryDependency.LIFECYCLE_PROCESS)
}
