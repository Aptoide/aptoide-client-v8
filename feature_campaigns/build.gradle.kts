plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.android.module)
  alias(libs.plugins.hilt)
  alias(libs.plugins.tests)
}

android {
  namespace = "cm.aptoide.pt.feature_campaigns"
  
  buildFeatures {
    buildConfig = true
  }
}

dependencies {
  implementation(projects.aptoideNetwork)

  implementation(libs.gms.play.services.ads)
  implementation(libs.timber)
}
