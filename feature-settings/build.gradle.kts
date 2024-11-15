plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.android.module)
  alias(libs.plugins.composable)
  alias(libs.plugins.hilt)
}

android {
  namespace = "cm.aptoide.pt.settings"
}

dependencies {
  implementation(project(ModuleDependency.ENVIRONMENT_INFO))

  //store
  implementation(libs.datastore)
}
