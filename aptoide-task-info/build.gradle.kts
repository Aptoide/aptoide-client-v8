plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.android.module)
  alias(libs.plugins.hilt)
  alias(libs.plugins.ksp)
}

android {
  namespace = "cm.aptoide.pt.downloads_database"

  ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
  }
}

dependencies {
  implementation(projects.installManager)

  //room
  implementation(libs.room)
  ksp(libs.room.compiler)
  implementation(libs.room.ktx)
}
