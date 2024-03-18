plugins {
  id(GradlePluginId.ANDROID_LIBRARY)
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
  implementation(LibraryDependency.LOTTIE)
}
