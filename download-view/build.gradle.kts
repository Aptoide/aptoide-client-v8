plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.android.module)
  alias(libs.plugins.composable)
  alias(libs.plugins.hilt)
}

android {
  namespace = "cm.aptoide.pt.download_view"
}

dependencies {
  implementation(projects.featureFlags)
  implementation(projects.featureApps)
  api(projects.installManager)
  implementation(projects.featureCampaigns)
  implementation(projects.extension)
  implementation(projects.networkListener)
  implementation(projects.installInfoMapper)
}
