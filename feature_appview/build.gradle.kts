plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.android.module)
  alias(libs.plugins.composable)
}

android {
  namespace = "cm.aptoide.pt.feature_appview"
}

dependencies {
  implementation(projects.aptoideNetwork)
  implementation(projects.featureApps)
  api(projects.featureReportApp)
  implementation(projects.featureEditorial)
  implementation(projects.downloadView)
  implementation(projects.featureReactions)
  implementation(projects.featureCampaigns)

  implementation(libs.custom.chrome.tab)
}
