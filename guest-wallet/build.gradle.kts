plugins {
  id(GradlePluginId.ANDROID_LIBRARY)
  id(GradlePluginId.ANDROID_MODULE)
  id(GradlePluginId.COMPOSABLE)
  id(GradlePluginId.HILT)
}

android {
  namespace = "cm.aptoide.pt.guest_wallet"

}

dependencies {
  api(project(ModuleDependency.FEATURE_PAYMENT))
  implementation(project(ModuleDependency.APTOIDE_NETWORK))

  //store
  implementation(LibraryDependency.DATASTORE)
}
