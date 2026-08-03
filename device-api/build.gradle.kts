plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.android.module)
  alias(libs.plugins.hilt)
}

android {
  namespace = "cm.aptoide.pt.device_api"
}

dependencies {
  implementation(projects.extension)
  implementation(projects.environmentInfo)

  api(libs.retrofit)
  api(libs.okhttp)
  api(libs.retrofit.gson.converter)
  api(libs.logging.interceptor)
}
