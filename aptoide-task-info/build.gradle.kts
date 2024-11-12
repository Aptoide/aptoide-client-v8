plugins {
  id(GradlePluginId.ANDROID_LIBRARY)
  id(GradlePluginId.ANDROID_MODULE)
  id(GradlePluginId.HILT)
  id(GradlePluginId.KOTLIN_KSP)
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
