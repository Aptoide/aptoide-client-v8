plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.android.module)
  alias(libs.plugins.composable)
  alias(libs.plugins.hilt)
}

android {
  namespace = "cm.aptoide.pt.wallet.wallet_info"
}

dependencies {
  implementation(projects.aptoideNetwork)
  implementation(projects.extension)
  implementation(projects.featureWallet.datastore)
}
