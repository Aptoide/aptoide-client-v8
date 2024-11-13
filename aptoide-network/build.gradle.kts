plugins {
  alias(libs.plugins.android.library)
  id(GradlePluginId.ANDROID_MODULE)
  id(GradlePluginId.HILT)
}

android {
  namespace = "cm.aptoide.pt.aptoide_network"
}

dependencies {
  implementation(project(ModuleDependency.EXTENSIONS))

  api(libs.retrofit)
  api(libs.okhttp)
  api(libs.retrofit.gson.converter)
  api(libs.logging.interceptor)
}
