buildscript {
  repositories {
    google()
    mavenCentral()
  }
}

plugins {
  alias(libs.plugins.android.application) apply false
  alias(libs.plugins.android.library) apply false
  alias(libs.plugins.ksp) apply false
  alias(libs.plugins.gms) apply false
  alias(libs.plugins.crashlytics) apply false
  alias(libs.plugins.junit5) apply false
  alias(libs.plugins.kotlin.android) apply false
  alias(libs.plugins.kotlin.compose.compiler) apply false
  alias(libs.plugins.hilt.android.plugin) apply false
}

tasks.register("clean", Delete::class) {
  delete(rootProject.layout.buildDirectory)
}
