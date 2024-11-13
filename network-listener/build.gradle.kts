plugins {
  alias(libs.plugins.android.library)
  id(GradlePluginId.ANDROID_MODULE)
  id(GradlePluginId.HILT)
}

android {
  namespace = "cm.aptoide.pt.network_listener"
}

dependencies {
  implementation(project(ModuleDependency.INSTALL_MANAGER))

  //WorkManager
  implementation(libs.work.manager)
}
