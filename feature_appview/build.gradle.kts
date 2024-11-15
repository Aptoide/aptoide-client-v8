plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.android.module)
  alias(libs.plugins.composable)
}

android {
  namespace = "cm.aptoide.pt.feature_appview"
}

dependencies {
  implementation(project(ModuleDependency.APTOIDE_NETWORK))
  implementation(project(ModuleDependency.FEATURE_APPS))
  api(project(ModuleDependency.FEATURE_REPORT_APP))
  implementation(project(ModuleDependency.FEATURE_EDITORIAL))
  implementation(project(ModuleDependency.DOWNLOAD_VIEW))
  implementation(project(ModuleDependency.FEATURE_REACTIONS))
  implementation(project(ModuleDependency.FEATURE_CAMPAIGNS))

  implementation(libs.custom.chrome.tab)
}
