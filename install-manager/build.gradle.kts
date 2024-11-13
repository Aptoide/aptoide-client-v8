plugins {
  alias(libs.plugins.android.library)
  id(GradlePluginId.ANDROID_MODULE)
  id(GradlePluginId.TESTS)
}

android {
  namespace = "cm.aptoide.pt.install_manager"
}

dependencies {
  implementation(project(ModuleDependency.EXTENSIONS))
}
