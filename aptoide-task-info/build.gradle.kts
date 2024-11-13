plugins {
  alias(libs.plugins.android.library)
  id(GradlePluginId.ANDROID_MODULE)
  id(GradlePluginId.HILT)
  alias(libs.plugins.ksp)
}

android {
  namespace = "cm.aptoide.pt.downloads_database"

  defaultConfig {
    ksp {
      arg("room.schemaLocation", "$projectDir/schemas")
    }
  }
}

dependencies {
  implementation(project(ModuleDependency.INSTALL_MANAGER))

  //room
  implementation(libs.room)
  ksp(libs.room.compiler)
  implementation(libs.room.ktx)
}
