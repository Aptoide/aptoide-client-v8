plugins {
  id(GradlePluginId.ANDROID_LIBRARY)
  id(GradlePluginId.ANDROID_MODULE)
  id(GradlePluginId.COMPOSABLE)
  id(GradlePluginId.HILT)
}

android {
  namespace = "cm.aptoide.pt.feature_home"
}

dependencies {
  implementation(project(ModuleDependency.APTOIDE_NETWORK))
  implementation(project(ModuleDependency.FEATURE_APPS))
  implementation(project(ModuleDependency.FEATURE_EDITORIAL))
  implementation(project(ModuleDependency.FEATURE_REACTIONS))
  implementation(project(ModuleDependency.EXTENSIONS))
}
