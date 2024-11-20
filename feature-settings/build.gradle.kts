plugins {
  alias(libs.plugins.android.library)
  id(GradlePluginId.ANDROID_MODULE)
  id(GradlePluginId.COMPOSABLE)
  id(GradlePluginId.HILT)
}

android {
  namespace = "cm.aptoide.pt.settings"
}

dependencies {
  implementation(project(ModuleDependency.ENVIRONMENT_INFO))

  //store
  implementation(libs.datastore)
}
