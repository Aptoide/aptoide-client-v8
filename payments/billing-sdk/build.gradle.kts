plugins {
  id(GradlePluginId.ANDROID_LIBRARY)
  id(GradlePluginId.ANDROID_MODULE)
  id(GradlePluginId.HILT)
}

android {
  namespace = "com.appcoins.billing.sdk"

  buildFeatures {
    aidl = true
    buildConfig = true
  }

  defaultConfig {
    buildConfigField("int", "SUPPORTED_API_VERSION", "3")
  }
}

dependencies {
  api(project(ModuleDependency.GUEST_WALLET))
  implementation(project(ModuleDependency.PRODUCT_INVENTORY))

  implementation(LibraryDependency.RETROFIT)
  implementation(LibraryDependency.GSON)
  implementation(LibraryDependency.APPCOINS_SDK_COMMUNICATION)
}
