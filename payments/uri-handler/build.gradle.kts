plugins {
  id(GradlePluginId.ANDROID_LIBRARY)
  id(GradlePluginId.ANDROID_MODULE)
  id(GradlePluginId.COMPOSABLE)
  id(GradlePluginId.HILT)
}

android {
  namespace = "com.appcoins.uri_handler"

  defaultConfig {
    manifestPlaceholders["payment_intent_filter_priority"] = "\${payment_intent_filter_priority}"
    manifestPlaceholders["payment_host"] = "\${payment_host}"
    manifestPlaceholders["applicationId"] = "\${applicationId}"
    manifestPlaceholders["adyenCheckoutScheme"] = "\${adyenCheckoutScheme}"
  }
}

dependencies {
  api(project(ModuleDependency.PAYMENT_MANAGER))
  implementation(project(ModuleDependency.OEM_EXTRACTOR))
}
