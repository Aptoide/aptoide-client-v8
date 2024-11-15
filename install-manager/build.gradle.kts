plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.android.module)
  alias(libs.plugins.tests)
}

android {
  namespace = "cm.aptoide.pt.install_manager"
}

dependencies {
  implementation(project(ModuleDependency.EXTENSIONS))
}
