plugins {
  id(GradlePluginId.ANDROID_LIBRARY)
  id(GradlePluginId.ANDROID_MODULE)
  id(GradlePluginId.HILT)
}

android {
  namespace = "com.appcoins.payments.network"
}

dependencies {
  api(LibraryDependency.RETROFIT)
  api(LibraryDependency.OK_HTTP)
  api(LibraryDependency.RETROFIT_GSON_CONVERTER)
  api(LibraryDependency.LOGGING_INTERCEPTOR)
}
