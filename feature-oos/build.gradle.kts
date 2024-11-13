plugins {
  alias(libs.plugins.android.library)
  id(GradlePluginId.ANDROID_MODULE)
  id(GradlePluginId.COMPOSABLE)
  id(GradlePluginId.HILT)
}

android {
  namespace = "cm.aptoide.pt.feature_oos"
}

dependencies {
  implementation(project(ModuleDependency.FEATURE_APPS))
  implementation(project(ModuleDependency.INSTALL_MANAGER))
  implementation(project(ModuleDependency.EXTENSIONS))
  implementation(project(ModuleDependency.INSTALL_INFO_MAPPER))
}
