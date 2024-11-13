plugins {
  alias(libs.plugins.android.library)
  id(GradlePluginId.ANDROID_MODULE)
  id(GradlePluginId.HILT)
  id(GradlePluginId.TESTS)
}

android {
  namespace = "cm.aptoide.pt.feature_campaigns"
}

dependencies {
  implementation(project(ModuleDependency.APTOIDE_NETWORK))

  implementation(libs.gms.play.services.ads)
}
