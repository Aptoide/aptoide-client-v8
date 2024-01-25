plugins {
  id(GradlePluginId.ANDROID_LIBRARY)
  id(GradlePluginId.ANDROID_MODULE)
  id(GradlePluginId.COMPOSABLE)
  id(GradlePluginId.HILT)
}

android {
  namespace = "com.appcoins.payment_method.adyen"
}

dependencies {
  api(project(ModuleDependency.PAYMENT_MANAGER))
  implementation(project(ModuleDependency.APTOIDE_NETWORK))

  implementation(LibraryDependency.ADYEN_CREDIT_CARD)
  implementation(LibraryDependency.ADYEN_3DS_2)
  implementation(LibraryDependency.ADYEN_REDIRECT)
}
