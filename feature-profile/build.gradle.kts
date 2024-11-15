plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.android.module)
  alias(libs.plugins.composable)
  alias(libs.plugins.hilt)
}

android {
  namespace = "cm.aptoide.pt.profile"
}

dependencies {
  implementation(project(ModuleDependency.FEATURE_SETTINGS))

  //store
  implementation(libs.datastore)
}
