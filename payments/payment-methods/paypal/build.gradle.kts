plugins {
  id(GradlePluginId.ANDROID_LIBRARY)
  id(GradlePluginId.ANDROID_MODULE)
  id(GradlePluginId.COMPOSABLE)
  id(GradlePluginId.HILT)
}

android {
  namespace = "com.appcoins.payment_method.paypal"
}

dependencies {
  api(files("libs/android-magnessdk-5.3.0.aar"))
  api(project(ModuleDependency.PAYMENT_MANAGER))
  implementation(project(ModuleDependency.APTOIDE_NETWORK))
  implementation(LibraryDependency.ACCOMPANIST_WEBVIEW)
}
