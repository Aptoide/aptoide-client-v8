plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.android.module)
  alias(libs.plugins.hilt)
}

android {
  namespace = "cm.aptoide.pt.install_info_mapper"
}

dependencies {
  implementation(projects.featureApps)
  implementation(projects.installManager)
}
