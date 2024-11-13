plugins {
  alias(libs.plugins.android.library)
  id(GradlePluginId.ANDROID_MODULE)
  alias(libs.plugins.junit5)
}

android {
  namespace = "cm.aptoide.pt.test"
}

dependencies {

  api(libs.junit)

  // New TDD dependencies
  api(libs.junit.jupiter.api)
  testRuntimeOnly(libs.junit.jupiter.engine)
  api(libs.junit.jupiter.params)
  api(libs.junit.jupiter.vantage)
  api(libs.coroutines.test)
  api(libs.turbine)
}
