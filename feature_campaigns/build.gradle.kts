plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.android.module)
  alias(libs.plugins.hilt)
  alias(libs.plugins.tests)
}

android {
  namespace = "cm.aptoide.pt.feature_campaigns"
}

dependencies {
  implementation(project(ModuleDependency.APTOIDE_NETWORK))

  implementation(libs.gms.play.services.ads)
}
