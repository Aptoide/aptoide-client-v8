import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  `kotlin-dsl`
}

java {
  sourceCompatibility = JavaVersion.VERSION_17
  targetCompatibility = JavaVersion.VERSION_17
}


kotlin {
  compilerOptions {
    jvmTarget = JvmTarget.JVM_17
  }
}

dependencies {
  compileOnly(libs.android.gradle.plugin)
  compileOnly(libs.kotlin.gradle.plugin)
  compileOnly(libs.hilt.android.gradle.plugin)
  compileOnly(libs.android.junit5.plugin)
  compileOnly(libs.google.services.plugin)
  compileOnly(libs.firebase.crashlytics.gradle)
  compileOnly(libs.ksp.plugin)
  compileOnly(libs.compose.gradle.plugin)
}

gradlePlugin {
  plugins {
    register("android-module") {
      id = "android-module"
      implementationClass = "plugin.AndroidModulePlugin"
    }
    register("composable") {
      id = "composable"
      implementationClass = "plugin.ComposablePlugin"
    }
    register("hilt") {
      id = "hilt"
      implementationClass = "plugin.HiltPlugin"
    }
    register("tests") {
      id = "tests"
      implementationClass = "plugin.TestsPlugin"
    }
  }
}
