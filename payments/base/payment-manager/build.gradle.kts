plugins {
  id(GradlePluginId.ANDROID_LIBRARY)
  id(GradlePluginId.ANDROID_MODULE)
  id(GradlePluginId.COMPOSABLE)
  id(GradlePluginId.HILT)
}

android {
  namespace = "com.appcoins.payment_manager"
}

dependencies {
  api(project(ModuleDependency.PAYMENT_PREFS))
  implementation(project(ModuleDependency.APTOIDE_NETWORK))
}
