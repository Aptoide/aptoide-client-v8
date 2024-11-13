plugins {
  alias(libs.plugins.android.library)
  id(GradlePluginId.ANDROID_MODULE)
  id(GradlePluginId.COMPOSABLE)
  id(GradlePluginId.HILT)
}

android {
  namespace = "cm.aptoide.pt.profile"
}

dependencies {
  implementation(project(ModuleDependency.FEATURE_SETTINGS))

  //store
  implementation(libs.datastore)
}
