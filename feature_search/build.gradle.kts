plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.android.module)
  alias(libs.plugins.composable)
  alias(libs.plugins.hilt)
  alias(libs.plugins.ksp)
}

android {
  namespace = "cm.aptoide.pt.feature_search"

  defaultConfig {
    ksp {
      arg("room.schemaLocation", "$projectDir/schemas")
    }
  }
}

dependencies {
  implementation(project(ModuleDependency.FEATURE_APPVIEW))
  implementation(project(ModuleDependency.APTOIDE_NETWORK))
  implementation(project(ModuleDependency.FEATURE_APPS))
  implementation(project(ModuleDependency.EXTENSIONS))

  //room
  implementation(libs.room)
  ksp(libs.room.compiler)
  implementation(libs.room.ktx)
}
