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
  implementation(projects.installManager)
  implementation(projects.aptoideNetwork)
  implementation(projects.extension)
  implementation(projects.installInfoMapper)
  implementation(projects.featureApps)

  implementation(libs.activity.ktx)
  implementation(libs.app.compat)

  //lifecycle
  implementation(libs.lifecycle.common)
  implementation(libs.lifecycle.process)

  //WorkManager
  implementation(libs.work.manager)
  implementation(libs.hilt.work)
}
