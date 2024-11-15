plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.android.module)
  alias(libs.plugins.composable)
  alias(libs.plugins.hilt)
  alias(libs.plugins.ksp)
}

android {
  namespace = "cm.aptoide.pt.feature_updates"
}

dependencies {
  implementation(project(ModuleDependency.APTOIDE_NETWORK))
  implementation(project(ModuleDependency.FEATURE_APPS))
  implementation(project(ModuleDependency.INSTALL_MANAGER))
  implementation(project(ModuleDependency.EXTENSIONS))
  implementation(project(ModuleDependency.INSTALL_INFO_MAPPER))

  //room
  implementation(libs.room)
  ksp(libs.room.compiler)
  implementation(libs.room.ktx)

  //Store
  implementation(LibraryDependency.DATASTORE)

  //WorkManager
  implementation(libs.work.manager)
  implementation(libs.hilt.work)
}
