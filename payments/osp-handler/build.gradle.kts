plugins {
  id(GradlePluginId.ANDROID_LIBRARY)
  id(GradlePluginId.ANDROID_MODULE)
  id(GradlePluginId.COMPOSABLE)
  id(GradlePluginId.HILT)
}

android {
  namespace = "cm.aptoide.pt.osp_handler"

  defaultConfig {
    manifestPlaceholders["payment_intent_filter_priority"] = "\${payment_intent_filter_priority}"
    manifestPlaceholders["payment_host"] = "\${payment_host}"
  }
}

dependencies {
  api(project(ModuleDependency.PAYMENT_MANAGER))
  implementation(project(ModuleDependency.EXTENSIONS))

  //store
  implementation(LibraryDependency.DATASTORE)

  implementation(LibraryDependency.APP_COMPAT)

  implementation(LibraryDependency.ACCOMPANIST_NAVIGATION)
}
