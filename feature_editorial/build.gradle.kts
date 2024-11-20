plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.android.module)
  alias(libs.plugins.composable)
  alias(libs.plugins.hilt)
}

android {
  namespace = "cm.aptoide.pt.feature_editorial"
}

dependencies {
  implementation(projects.aptoideNetwork)
  implementation(projects.featureApps)
  implementation(projects.extension)
}
