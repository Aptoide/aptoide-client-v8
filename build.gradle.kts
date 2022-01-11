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

ext {


  // Room versions
  //ROOM_VERSION = '2.2.4'

  // Google Android Support
  //APPCOMPAT_VERSION = '1.1.0'
  //CORE_VERSION = '1.2.0'
  //ANDROIDX_ANNOTATION_VERSION = '1.1.0'

  //MATERIAL_VERSION = '1.2.0-beta01'

  // Google Play Services
/*
  PLAY_SERVICES_BASEMENT_VERSION = '16.1.0'
  PLAY_SERVICES_ADS_VERSION = '17.2.1'
  PLAY_SERVICES_SAFETYNET_VERSION = '16.0.0'
  PLAY_SERVICES_AUTH_VERSION = '16.0.1'
  PLAY_SERVICES_LOCATION_VERSION = '17.0.0'

*/

  // Retrofit
  //RETROFIT_VERSION = '2.1.0'


  // Facebook Android SDK
  //FACEBOOK_ANDROID_SDK_VERSION = '7.1.0'


  //OKHTTP_VERSION = '4.2.2'

  //CONSTRAINT_LAYOUT_VERSION = '2.0.4'


  //LOTTIE_VERSION = '2.7.0'

  //kotlin
  //COROUTINES_VERSION = '1.3.7'
}

// see 'Multi-module reports' in https://developer.android.com/studio/test/command-line.html
//apply plugin: 'android-reporting'
