plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.android.module)
  alias(libs.plugins.composable)
  alias(libs.plugins.hilt)
  alias(libs.plugins.tests)
}

android {
  namespace = "cm.aptoide.pt.play_and_earn.exchange"
}

dependencies {
  implementation(projects.aptoideNetwork)
  implementation(projects.exceptionHandler)
  implementation(projects.extension)
  implementation(projects.featureWallet.authorization)
  implementation(projects.featureWallet.walletInfo)

  // The wallet units use case reads the wallet address from this store.
  testImplementation(projects.featureWallet.datastore)
  testImplementation(libs.datastore)
}
