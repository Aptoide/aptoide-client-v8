plugins {
  id(GradlePluginId.ANDROID_LIBRARY)
  id(GradlePluginId.ANDROID_MODULE)
  id(GradlePluginId.COMPOSABLE)
  id(GradlePluginId.HILT)
}

android {
  namespace = "com.appcoins.payment_method.paypal.presentation"
}

dependencies {
  implementation(project(ModuleDependency.PAYPAL))
  api(project(ModuleDependency.MAGNES))
  api(project(ModuleDependency.PAYMENT_MANAGER))
  implementation(project(ModuleDependency.PAYMENTS_NETWORK))
  implementation(LibraryDependency.ACCOMPANIST_WEBVIEW)
}
