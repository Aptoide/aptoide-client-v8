buildscript {


  extra.apply {
    set("APTOIDE_THEME", "default")
    set("MARKET_NAME", "Aptoide")
  }

  repositories {
    google()
    mavenCentral()
  }
  dependencies {
    classpath("com.android.tools.build:gradle:${GradlePluginVersion.ANDROID_GRADLE}")
    classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${CoreVersion.KOTLIN}")
    classpath("com.google.dagger:hilt-android-gradle-plugin:${GradlePluginVersion.HILT}")
    classpath("de.mannodermaus.gradle.plugins:android-junit5:1.8.2.1")
  }
}

allprojects {
  repositories {
    google()
    mavenCentral()
  }
}


tasks.register("clean", Delete::class) {
  delete(rootProject.buildDir)
}
