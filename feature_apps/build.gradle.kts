plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.android.module)
  alias(libs.plugins.composable)
  alias(libs.plugins.hilt)
}

android {
  namespace = "cm.aptoide.pt.feature_apps"
}

dependencies {
  implementation(project(ModuleDependency.APTOIDE_NETWORK))
  implementation(project(ModuleDependency.EXTENSIONS))
  api(project(ModuleDependency.FEATURE_CAMPAIGNS))
}
