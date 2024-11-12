plugins {
  id(GradlePluginId.ANDROID_LIBRARY)
  id(GradlePluginId.ANDROID_MODULE)
  id(GradlePluginId.JUNIT5_PLUGIN)
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
