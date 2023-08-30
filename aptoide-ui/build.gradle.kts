plugins {
  id(GradlePluginId.ANDROID_LIBRARY)
  id(GradlePluginId.ANDROID_MODULE)
  id(GradlePluginId.COMPOSABLE)
  id(GradlePluginId.HILT)
}

android {
  namespace = "cm.aptoide.pt.aptoide_ui"
}

dependencies {
  //Accompanist
  implementation(LibraryDependency.ACCOMPANIST_WEBVIEW)
  implementation(LibraryDependency.ACCOMPANIST_NAVIGATION)
}
