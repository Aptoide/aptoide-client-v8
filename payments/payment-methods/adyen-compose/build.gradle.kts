plugins {
  id(GradlePluginId.ANDROID_LIBRARY)
  id(GradlePluginId.ANDROID_MODULE)
  id(GradlePluginId.COMPOSABLE)
  id(GradlePluginId.HILT)
}

android {
  namespace = "com.appcoins.payment_method.adyen.presentation"
}

dependencies {
  implementation(project(ModuleDependency.ADYEN))

  implementation(LibraryDependency.ADYEN_CREDIT_CARD)
  implementation(LibraryDependency.ADYEN_3DS_2)
  implementation(LibraryDependency.ADYEN_REDIRECT)
}
