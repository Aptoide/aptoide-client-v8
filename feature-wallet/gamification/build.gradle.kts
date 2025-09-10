plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.android.module)
  alias(libs.plugins.hilt)
  alias(libs.plugins.tests)
}

android {
  namespace = "cm.aptoide.pt.wallet.gamification"
}

dependencies {
  implementation(projects.aptoideNetwork)
  implementation(projects.featureWallet.datastore)
}
