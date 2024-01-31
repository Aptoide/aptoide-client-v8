plugins {
  id(GradlePluginId.ANDROID_LIBRARY)
  id(GradlePluginId.ANDROID_MODULE)
  id(GradlePluginId.HILT)
}

android {
  namespace = "com.appcoins.product_inventory"

}

dependencies {
  api(project(ModuleDependency.PAYMENTS_ARCH))
  implementation(project(ModuleDependency.APTOIDE_NETWORK))
}
