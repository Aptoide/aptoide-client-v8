plugins {
  id(GradlePluginId.ANDROID_LIBRARY)
  id(GradlePluginId.ANDROID_MODULE)
  id(GradlePluginId.COMPOSABLE)
  id(GradlePluginId.HILT)
  id(GradlePluginId.KOTLIN_KSP)
}

android {
  namespace = "cm.aptoide.pt.feature_updates"
}

dependencies {
  implementation(project(ModuleDependency.APTOIDE_NETWORK))
  implementation(project(ModuleDependency.FEATURE_APPS))
  implementation(project(ModuleDependency.INSTALL_MANAGER))
  implementation(project(ModuleDependency.EXTENSIONS))

  //room
  implementation(LibraryDependency.ROOM)
  ksp(LibraryDependency.ROOM_COMPILER)
  implementation(LibraryDependency.ROOM_KTX)

  //Store
  implementation(LibraryDependency.DATASTORE)

  //WorkManager
  implementation(LibraryDependency.WORK_MANAGER)
  implementation(LibraryDependency.HILT_WORK)
}
