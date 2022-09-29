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
  }
}

allprojects {
  repositories {
    google()
    mavenCentral()
    maven { setUrl("https://jitpack.io")}
  }
}

tasks.register("clean", Delete::class) {
  delete(rootProject.buildDir)
}
