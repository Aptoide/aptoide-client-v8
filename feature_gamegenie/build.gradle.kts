plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.android.module)
  alias(libs.plugins.hilt)
  alias(libs.plugins.ksp)
}

android {
  namespace = "cm.aptoide.pt.feature_gamegenie"

  ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
  }
}

dependencies {
  implementation(projects.aptoideNetwork)
  implementation(projects.featureApps)
  implementation(projects.featureCategories)
  implementation(projects.installManager)

  implementation(libs.lifecycle.runtime.ktx)
  implementation(libs.room)
  implementation(libs.room.ktx)
  ksp(libs.room.compiler)
}
