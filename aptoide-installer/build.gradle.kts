plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.android.module)
  alias(libs.plugins.hilt)
  alias(libs.plugins.composable)
  alias(libs.plugins.ksp)
}

android {
  namespace = "cm.aptoide.pt.installer"
}

dependencies {
  implementation(project(ModuleDependency.INSTALL_MANAGER))
  implementation(project(ModuleDependency.APTOIDE_NETWORK))
  implementation(project(ModuleDependency.EXTENSIONS))
  implementation(project(ModuleDependency.INSTALL_INFO_MAPPER))
  implementation(project(ModuleDependency.FEATURE_APPS))

  implementation(libs.activity.ktx)
  implementation(libs.app.compat)

  //lifecycle
  implementation(libs.lifecycle.common)
  implementation(libs.lifecycle.process)

  //WorkManager
  implementation(libs.work.manager)
  implementation(libs.hilt.work)
}
