plugins {
  id(GradlePluginId.ANDROID_LIBRARY)
  id(GradlePluginId.ANDROID_MODULE)
  id(GradlePluginId.COMPOSABLE)
  id(GradlePluginId.HILT)
  id(GradlePluginId.KOTLIN_KSP) version GradlePluginVersion.KSP
}

android {
  namespace = "cm.aptoide.pt.feature_search"
}

dependencies {
  implementation(project(ModuleDependency.FEATURE_APPVIEW))
  implementation(project(ModuleDependency.APTOIDE_NETWORK))
  implementation(project(ModuleDependency.APTOIDE_UI))

  //room
  implementation(LibraryDependency.ROOM)
  ksp(LibraryDependency.ROOM_COMPILER)
  implementation(LibraryDependency.ROOM_KTX)
}
