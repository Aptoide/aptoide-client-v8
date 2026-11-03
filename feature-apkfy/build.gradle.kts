plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.android.module)
  alias(libs.plugins.composable)
  alias(libs.plugins.hilt)
  alias(libs.plugins.tests)
}

android {
  namespace = "cm.aptoide.pt.feature_apkfy"
}

dependencies {
  implementation(projects.aptoideNetwork)
  implementation(projects.deviceApi)
  implementation(projects.extension)
  implementation(projects.featureApps)
  implementation(libs.datastore)
}
