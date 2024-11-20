plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.android.module)
  alias(libs.plugins.hilt)
}

android {
  namespace = "cm.aptoide.pt.network_listener"
}

dependencies {
  implementation(projects.installManager)

  //WorkManager
  implementation(libs.work.manager)
}
