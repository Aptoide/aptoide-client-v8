plugins {
  id(GradlePluginId.ANDROID_LIBRARY)
  id(GradlePluginId.ANDROID_MODULE)
  id(GradlePluginId.COMPOSABLE)
  id(GradlePluginId.HILT)
}

android {
  namespace = "cm.aptoide.pt.payment_manager"

  defaultConfig {
    manifestPlaceholders["payment_intent_filter_priority"] = "\${payment_intent_filter_priority}"
    manifestPlaceholders["payment_host"] = "\${payment_host}"
  }
}

dependencies {
  implementation(project(ModuleDependency.APTOIDE_NETWORK))
  implementation(project(ModuleDependency.EXTENSIONS))

  //store
  implementation(LibraryDependency.DATASTORE)
}
