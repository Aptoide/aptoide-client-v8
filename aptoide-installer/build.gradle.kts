plugins {
  id(GradlePluginId.ANDROID_LIBRARY)
  id(GradlePluginId.ANDROID_MODULE)
  id(GradlePluginId.HILT)
  id(GradlePluginId.COMPOSABLE)
  id(GradlePluginId.KOTLIN_KSP)
}

android {
  namespace = "cm.aptoide.pt.installer"
}

dependencies {
  implementation(project(ModuleDependency.INSTALL_MANAGER))
  implementation(project(ModuleDependency.APTOIDE_NETWORK))
  implementation(project(ModuleDependency.EXTENSIONS))
  implementation(project(ModuleDependency.INSTALL_INFO_MAPPER))
  implementation(project(ModuleDependency.FEATURE_APPS))

  implementation(LibraryDependency.ACTIVITY_KTX)
  implementation(LibraryDependency.APP_COMPAT)

  //lifecycle
  implementation(LibraryDependency.LIFECYCLE_COMMON)
  implementation(LibraryDependency.LIFECYCLE_PROCESS)

  //WorkManager
  implementation(LibraryDependency.WORK_MANAGER)
  implementation(LibraryDependency.HILT_WORK)
}
