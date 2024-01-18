plugins {
  id(GradlePluginId.ANDROID_LIBRARY)
  id(GradlePluginId.ANDROID_MODULE)
  id(GradlePluginId.HILT)
}

android {
  namespace = "com.appcoins.billing.sdk"

  buildFeatures {
    aidl = true
  }
}

dependencies {
  implementation(LibraryDependency.APPCOINS_SDK_COMMUNICATION)
}
