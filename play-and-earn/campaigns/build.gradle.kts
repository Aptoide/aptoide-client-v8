plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.android.module)
  alias(libs.plugins.composable)
  alias(libs.plugins.hilt)
}

android {
  namespace = "cm.aptoide.pt.play_and_earn.campaigns"
}

dependencies {
  implementation(projects.aptoideNetwork)
  implementation(projects.extension)
  implementation(projects.featureApps)
  implementation(projects.featureWallet.authorization)
}
