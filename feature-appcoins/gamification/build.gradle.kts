plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.android.module)
  alias(libs.plugins.hilt)
  alias(libs.plugins.tests)
}

android {
  namespace = "cm.aptoide.pt.feature_appcoins.gamification"
}

dependencies {
  implementation(projects.aptoideNetwork)
  implementation(projects.featureAppcoins.datastore)
}
