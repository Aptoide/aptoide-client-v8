plugins {
  id(GradlePluginId.ANDROID_LIBRARY)
  id(GradlePluginId.ANDROID_MODULE)
  id(GradlePluginId.COMPOSABLE)
  id(GradlePluginId.HILT)
}

android {
  namespace = "cm.aptoide.pt.payment_method.adyen"
}

dependencies {
  api(project(ModuleDependency.PAYMENT_MANAGER))
  implementation(project(ModuleDependency.APTOIDE_NETWORK))
  implementation(project(ModuleDependency.EXTENSIONS))

  implementation(LibraryDependency.COIL_COMPOSE)
  implementation(LibraryDependency.GSON)
  implementation(LibraryDependency.ADYEN_CREDIT_CARD)
  implementation(LibraryDependency.ADYEN_3DS_2)
  implementation(LibraryDependency.ADYEN_REDIRECT)
}