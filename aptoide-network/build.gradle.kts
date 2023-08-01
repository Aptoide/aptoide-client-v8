plugins {
  id(GradlePluginId.ANDROID_LIBRARY)
  id(GradlePluginId.ANDROID_MODULE)
  id(GradlePluginId.HILT)
}

android {
  namespace = "cm.aptoide.pt.aptoide_network"
}

dependencies {
  implementation(project(ModuleDependency.EXTENSIONS))

  api(LibraryDependency.RETROFIT)
  api(LibraryDependency.OK_HTTP)
  api(LibraryDependency.RETROFIT_GSON_CONVERTER)
  api(LibraryDependency.LOGGING_INTERCEPTOR)
}
