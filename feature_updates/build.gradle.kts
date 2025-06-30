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
  implementation(projects.aptoideNetwork)
  implementation(projects.featureApps)
  implementation(projects.installManager)
  implementation(projects.extension)
  implementation(projects.installInfoMapper)
  implementation(projects.featureFlags)

  //room
  implementation(libs.room)
  ksp(libs.room.compiler)
  implementation(libs.room.ktx)

  //Store
  implementation(libs.datastore)

  //WorkManager
  implementation(libs.work.manager)
  implementation(libs.hilt.work)
}
