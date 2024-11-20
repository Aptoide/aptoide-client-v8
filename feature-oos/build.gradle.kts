plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.android.module)
  alias(libs.plugins.composable)
  alias(libs.plugins.hilt)
}

android {
  namespace = "cm.aptoide.pt.feature_oos"
}

dependencies {
  implementation(projects.featureApps)
  implementation(projects.installManager)
  implementation(projects.extension)
  implementation(projects.installInfoMapper)
}
