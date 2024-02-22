plugins {
  id(GradlePluginId.ANDROID_LIBRARY)
  id(GradlePluginId.ANDROID_MODULE)
  id(GradlePluginId.COMPOSABLE)
  id(GradlePluginId.HILT)
}

android {
  namespace = "com.appcoins.payment_manager.presentation"
}

dependencies {
  implementation(project(ModuleDependency.PAYMENT_MANAGER))
}
