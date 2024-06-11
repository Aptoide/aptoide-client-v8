plugins {
  id(GradlePluginId.ANDROID_LIBRARY)
  id(GradlePluginId.ANDROID_MODULE)
  id(GradlePluginId.COMPOSABLE)
  id(GradlePluginId.HILT)
}

android {
  namespace = "cm.aptoide.pt.download_view"
}

dependencies {
  implementation(project(ModuleDependency.FEATURE_FLAGS))
  implementation(project(ModuleDependency.FEATURE_APPS))
  api(project(ModuleDependency.INSTALL_MANAGER))
  implementation(project(ModuleDependency.FEATURE_CAMPAIGNS))
  implementation(project(ModuleDependency.EXTENSIONS))
  implementation(project(ModuleDependency.NETWORK_LISTENER))
  implementation(project(ModuleDependency.INSTALL_INFO_MAPPER))
}
