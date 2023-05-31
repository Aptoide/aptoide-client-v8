plugins {
  id(GradlePluginId.ANDROID_LIBRARY)
  id(GradlePluginId.ANDROID_MODULE)
  id(GradlePluginId.HILT)
  id(GradlePluginId.KOTLIN_KSP) version GradlePluginVersion.KSP
}

android {
  namespace = "cm.aptoide.pt.installedapps"
}

dependencies {
  //room
  implementation(LibraryDependency.ROOM)
  ksp(LibraryDependency.ROOM_COMPILER)
  implementation(LibraryDependency.ROOM_KTX)
}
