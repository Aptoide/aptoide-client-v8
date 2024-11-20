plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.android.module)
  alias(libs.plugins.composable)
  alias(libs.plugins.hilt)
}

android {
  namespace = "cm.aptoide.pt.feature_home"
}

dependencies {
  implementation(projects.aptoideNetwork)
  implementation(projects.featureApps)
  implementation(projects.featureEditorial)
  implementation(projects.featureReactions)
  implementation(projects.extension)
  implementation(projects.featureBonus)
}
