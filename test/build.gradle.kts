plugins {
  id(GradlePluginId.ANDROID_LIBRARY)
  id(GradlePluginId.ANDROID_MODULE)
  id(GradlePluginId.JUNIT5_PLUGIN)
}

android {
  namespace = "cm.aptoide.pt.test"
}

dependencies {

  api(TestLibraryDependency.JUNIT)

  // New TDD dependencies
  api(TestLibraryDependency.JUNIT_JUPITER_API)
  testRuntimeOnly(TestLibraryDependency.JUNIT_JUPITER_ENGINE)
  api(TestLibraryDependency.JUNIT_JUPITER_PARAMS)
  api(TestLibraryDependency.JUNIT_JUPITER_VANTAGE)
  api(TestLibraryDependency.COROUTINES_TEST)
  api(TestLibraryDependency.TURBINE)
}
