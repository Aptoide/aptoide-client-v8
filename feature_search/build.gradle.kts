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
  implementation(projects.featureAppview)
  implementation(projects.aptoideNetwork)
  implementation(projects.featureApps)
  implementation(projects.extension)

  //room
  implementation(libs.room)
  ksp(libs.room.compiler)
  implementation(libs.room.ktx)
}
