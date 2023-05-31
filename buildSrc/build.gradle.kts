plugins {
  `java-gradle-plugin`
  `kotlin-dsl`
}

// The kotlin-dsl plugin requires a repository to be declared
repositories {
  google()
  mavenCentral()
}

dependencies {
  implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.21")
  // Android gradle plugin will allow us to access Android specific features
  implementation("com.android.tools.build:gradle:8.0.2")
  implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.21")
  // Required by HILT. To override older version pushed by gradle plugin
  implementation("com.squareup:javapoet:1.13.0")
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
