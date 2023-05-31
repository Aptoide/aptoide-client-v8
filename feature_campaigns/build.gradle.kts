plugins {
  id(GradlePluginId.ANDROID_LIBRARY)
  id(GradlePluginId.ANDROID_MODULE)
  id(GradlePluginId.HILT)
  id(GradlePluginId.TESTS)
}

android {
  namespace = "cm.aptoide.pt.feature_campaigns"
}

dependencies {
  implementation(project(ModuleDependency.APTOIDE_NETWORK))

  implementation(LibraryDependency.GMS_PLAY_SERVICES_ADS)
}
