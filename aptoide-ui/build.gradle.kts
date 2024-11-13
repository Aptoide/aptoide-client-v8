plugins {
  alias(libs.plugins.android.library)
  id(GradlePluginId.ANDROID_MODULE)
  id(GradlePluginId.COMPOSABLE)
  id(GradlePluginId.HILT)
}

android {
  namespace = "cm.aptoide.pt.aptoide_ui"
}

dependencies {
  //Accompanist
  implementation(project(ModuleDependency.EXTENSIONS))
}
