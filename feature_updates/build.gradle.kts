plugins {
  id(GradlePluginId.ANDROID_LIBRARY)
  id(GradlePluginId.ANDROID_MODULE)
  id(GradlePluginId.COMPOSABLE)
  id(GradlePluginId.HILT)
}

android {
  namespace = "cm.aptoide.pt.feature_updates"
}

dependencies {
  implementation(project(ModuleDependency.APTOIDE_UI))
  implementation(project(ModuleDependency.INSTALLED_APPS))
}