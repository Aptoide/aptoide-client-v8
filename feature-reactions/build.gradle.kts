plugins {
  alias(libs.plugins.android.library)
  id(GradlePluginId.ANDROID_MODULE)
  id(GradlePluginId.COMPOSABLE)
  id(GradlePluginId.HILT)
}

android {
  namespace = "cm.aptoide.pt.feature_reactions"
}

dependencies {
  implementation(project(ModuleDependency.APTOIDE_NETWORK))

  //animations
  implementation(libs.lottie)
}
