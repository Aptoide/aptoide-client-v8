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
  api(project(ModuleDependency.PAYMENTS_ARCH))
  api(project(ModuleDependency.PAYMENT_PREFS))
  api(project(ModuleDependency.PRODUCT_INVENTORY))
  api(project(ModuleDependency.PAYMENTS_NETWORK))
}
