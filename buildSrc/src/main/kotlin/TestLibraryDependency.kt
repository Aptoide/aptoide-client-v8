object TestLibraryVersionOldModules {
  const val JUNIT = "4.13.2"
  const val KOTLIN_JUNIT = "4.13.2"
  const val MOCKITO_CORE = "2.27.0"
  const val MOCKITO_CORE_ANDROID = "2.28.2"
  const val ESPRESSO = "3.2.0"
  const val RULES = "1.1.0"
  const val RUNNER = "1.1.0"
  const val MOCK_WEB_SERVER = "3.4.1"
}

object TestLibraryVersion {
  const val ROOM_TESTING = "2.4.0"
  const val JUNIT = "4.13.2"
  const val JUNIT5 = "5.7.2"
  const val TURBINE = "0.9.0"
  const val JUNIT_ANDROIDX = "1.1.3"
  const val ESPRESSO_CORE = "3.4.0"
}

object TestLibraryDependency {
  const val ROOM_TESTING = "androidx.room:room-testing:${TestLibraryVersion.ROOM_TESTING}"
  const val JUNIT = "junit:junit:${TestLibraryVersion.JUNIT}"
  const val JUNIT_ANDROIDX = "androidx.test.ext:junit:${TestLibraryVersion.JUNIT_ANDROIDX}"
  const val ESPRESSO_CORE =
      "androidx.test.espresso:espresso-core:${TestLibraryVersion.ESPRESSO_CORE}"
  const val COROUTINES_TEST =
      "org.jetbrains.kotlinx:kotlinx-coroutines-test:${CoreVersion.COROUTINES}"
  const val MOCKITO_CORE = "org.mockito:mockito-core:${TestLibraryVersionOldModules.MOCKITO_CORE}"

  const val JUNIT_JUPITER_API = "org.junit.jupiter:junit-jupiter-api:${TestLibraryVersion.JUNIT5}"
  const val JUNIT_JUPITER_ENGINE = "org.junit.jupiter:junit-jupiter-engine:${TestLibraryVersion.JUNIT5}"
  const val JUNIT_JUPITER_PARAMS = "org.junit.jupiter:junit-jupiter-params:${TestLibraryVersion.JUNIT5}"
  const val JUNIT_JUPITER_VANTAGE = "org.junit.vintage:junit-vintage-engine:${TestLibraryVersion.JUNIT5}"
  const val TURBINE = "app.cash.turbine:turbine:${TestLibraryVersion.TURBINE}"
}