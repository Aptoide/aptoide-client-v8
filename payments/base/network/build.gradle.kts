plugins {
  id(GradlePluginId.ANDROID_LIBRARY)
  id(GradlePluginId.ANDROID_MODULE)
  id(GradlePluginId.HILT)
}

android {
  namespace = "com.appcoins.payments.network"
}

dependencies {
  api(project(ModuleDependency.APTOIDE_NETWORK))
}
