plugins {
  alias(libs.plugins.android.library)
  id(GradlePluginId.ANDROID_MODULE)
  id(GradlePluginId.COMPOSABLE)
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
