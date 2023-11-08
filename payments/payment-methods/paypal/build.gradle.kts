plugins {
  id(GradlePluginId.ANDROID_LIBRARY)
  id(GradlePluginId.ANDROID_MODULE)
  id(GradlePluginId.COMPOSABLE)
  id(GradlePluginId.HILT)
}

android {
  namespace = "cm.aptoide.pt.payment_method.paypal"
}

dependencies {
  api(project(ModuleDependency.PAYMENT_MANAGER))
  implementation(project(ModuleDependency.APTOIDE_NETWORK))
  implementation(project(ModuleDependency.EXTENSIONS))
  implementation(LibraryDependency.GSON)
  implementation(LibraryDependency.ACCOMPANIST_WEBVIEW)
}
