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
  implementation("org.jetbrains.kotlin:kotlin-stdlib:2.0.21")
  // Android gradle plugin will allow us to access Android specific features
  implementation("com.android.tools.build:gradle:8.7.1")
  implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.0.21")
  // Required by HILT. To override older version pushed by gradle plugin
  implementation("com.squareup:javapoet:1.13.0")
}
