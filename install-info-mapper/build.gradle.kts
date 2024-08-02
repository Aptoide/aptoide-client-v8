plugins {
  id(GradlePluginId.ANDROID_LIBRARY)
  id(GradlePluginId.ANDROID_MODULE)
  id(GradlePluginId.HILT)
}

android {
  namespace = "cm.aptoide.pt.install_info_mapper"
}

dependencies {
  implementation(project(ModuleDependency.FEATURE_APPS))
  implementation(project(ModuleDependency.INSTALL_MANAGER))
}
