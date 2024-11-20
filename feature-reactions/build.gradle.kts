plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.android.module)
  alias(libs.plugins.composable)
  alias(libs.plugins.hilt)
}

android {
  namespace = "cm.aptoide.pt.feature_reactions"
}

dependencies {
  implementation(projects.aptoideNetwork)

  //animations
  implementation(libs.lottie)
}
