plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.android.module)
  alias(libs.plugins.hilt)
}

android {
  namespace = "cm.aptoide.pt.wallet.datastore"
}

dependencies {
  implementation(libs.datastore)
}
