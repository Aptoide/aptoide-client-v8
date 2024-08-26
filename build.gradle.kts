val compileSdkVersion by extra { AndroidConfig.COMPILE_SDK }
val minSdkVersion by extra { AndroidConfig.MIN_SDK }

// Billing SDK
val supportedSdkVersion by extra { "3" }

// Kotlin
val coreKtxVersion by rootProject.extra { LibraryVersion.CORE_KTX }
val kotlinxCoroutinesAndroidVersion by rootProject.extra { CoreVersion.COROUTINES }

// KSP
val kspVersion by extra { GradlePluginVersion.KSP }

// Hilt
val hiltAndroidVersion by rootProject.extra { LibraryVersion.HILT }
val daggerHiltCompilerVersion by rootProject.extra { LibraryVersion.HILT }

// Apache
val apacheCommonsTextVersion by rootProject.extra { LibraryVersion.APACHE_COMMONS_TEXT }

// Android Material
val androidxActivityVersion by rootProject.extra { LibraryVersion.ACTIVITY_KTX }

// Adyen
val adyenVersion by rootProject.extra { LibraryVersion.ADYEN_VERSION }

// Compose
val navigationComposeVersion by rootProject.extra { LibraryVersion.NAVIGATION_COMPOSE }
val hiltNavigationComposeVersion by rootProject.extra { LibraryVersion.HILT_NAV_COMPOSE }
val composeMaterialVersion by rootProject.extra { LibraryVersion.COMPOSE }
val lifecycleViewModelComposeVersion by rootProject.extra { LibraryVersion.VIEWMODEL_COMPOSE }

buildscript {

  repositories {
    google()
    mavenCentral()
  }
  dependencies {
    classpath("com.android.tools.build:gradle:${GradlePluginVersion.ANDROID_GRADLE}")
    classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${GradlePluginVersion.KOTLIN}")
    classpath("com.google.dagger:hilt-android-gradle-plugin:${GradlePluginVersion.HILT}")
    classpath("de.mannodermaus.gradle.plugins:android-junit5:${GradlePluginVersion.JUNIT5}")
    classpath("com.google.gms:google-services:${GradlePluginVersion.GMS}")
    classpath("com.google.firebase:firebase-crashlytics-gradle:${GradlePluginVersion.CRASHLYTICS}")
    classpath("com.google.devtools.ksp:symbol-processing-gradle-plugin:${GradlePluginVersion.KSP}")
    classpath("org.jetbrains.kotlin:compose-compiler-gradle-plugin:${GradlePluginVersion.KOTLIN}")
  }
}

allprojects {
  repositories {
    google()
    mavenCentral()
  }
}


tasks.register("clean", Delete::class) {
  delete(rootProject.layout.buildDirectory)
}
