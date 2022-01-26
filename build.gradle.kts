buildscript {
  repositories {
    mavenLocal()
    google()
    jcenter()
  }
  dependencies {
    classpath("com.android.tools.build:gradle:${GradlePluginVersion.ANDROID_GRADLE}")
    classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${CoreVersion.KOTLIN}")
    classpath("org.jetbrains.kotlin:kotlin-android-extensions:${CoreVersion.KOTLIN}")
    classpath("com.google.dagger:hilt-android-gradle-plugin:${GradlePluginVersion.HILT}")
  }
}

allprojects {
  repositories {
    jcenter()
    google()
    maven(url = "https://www.jitpack.io")

    maven(url = "https://maven.google.com")
    flatDir {
      dirs("libs")
    }
  }
}

tasks.register("clean", Delete::class) {
  delete(rootProject.buildDir)
}
