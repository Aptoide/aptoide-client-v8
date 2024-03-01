val compileSdkVersion by extra { AndroidConfig.COMPILE_SDK }
val minSdkVersion by extra { AndroidConfig.MIN_SDK }

val kotlinCompilerExtensionVersion by extra { LibraryVersion.KOTLIN_COMPILER_EXTENSION }

val supportedSdkVersion by extra { "3" }

val coreKtsVersion by rootProject.extra { LibraryVersion.CORE_KTX }
val kotlinStdlibJdkVersion by rootProject.extra { CoreVersion.KOTLIN }
val kotlinxCoroutinesAndroidVersion by rootProject.extra { CoreVersion.COROUTINES }
val kotlinxCoroutinesCoreVersion by rootProject.extra { CoreVersion.COROUTINES }
val timberVersion by rootProject.extra { LibraryVersion.TIMBER }

// Hilt
val hiltAndroidVersion by rootProject.extra { LibraryVersion.HILT }
val daggerHiltCompilerVersion by rootProject.extra { LibraryVersion.HILT }
val androidxHiltCompilerVersion by rootProject.extra { LibraryVersion.HILT_COMPILER }

// Apache
val apacheCommonsTextVersion by rootProject.extra { LibraryVersion.APACHE_COMMONS_TEXT }

// Android Material
val androidMaterialVersion by rootProject.extra { LibraryVersion.MATERIAL }

// Adyen
val adyenVersion by rootProject.extra { LibraryVersion.ADYEN_VERSION }

// Catappult
val catappultCommunicationVersion by rootProject.extra { LibraryVersion.APPCOINS_SDK }

// Compose
val appCompatVersion by rootProject.extra { LibraryVersion.APP_COMPAT }
val activityKtxVersion by rootProject.extra { LibraryVersion.ACTIVITY_KTX }
val navigationComposeVersion by rootProject.extra { LibraryVersion.NAVIGATION_COMPOSE }
val hiltNavigationComposeVersion by rootProject.extra { LibraryVersion.HILT_NAV_COMPOSE }
val composeMaterialVersion by rootProject.extra { LibraryVersion.COMPOSE }
val composeAnimationVersion by rootProject.extra { LibraryVersion.COMPOSE }
val composeUiToolingVersion by rootProject.extra { LibraryVersion.COMPOSE }
val lifecycleViewModelComposeVersion by rootProject.extra { LibraryVersion.VIEWMODEL_COMPOSE }
val materialIconsExtendedVersion by rootProject.extra { LibraryVersion.MATERIAL_ICONS_EXTENDED }
val coilComposeVersion by rootProject.extra { LibraryVersion.COIL }

buildscript {

  repositories {
    google()
    mavenCentral()
  }
  dependencies {
    classpath("com.android.tools.build:gradle:${GradlePluginVersion.ANDROID_GRADLE}")
    classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${CoreVersion.KOTLIN}")
    classpath("com.google.dagger:hilt-android-gradle-plugin:${GradlePluginVersion.HILT}")
    classpath("de.mannodermaus.gradle.plugins:android-junit5:${GradlePluginVersion.JUNIT5}")
    classpath("com.google.gms:google-services:${GradlePluginVersion.GMS}")
    classpath("com.google.firebase:firebase-crashlytics-gradle:${GradlePluginVersion.CRASHLYTICS}")
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
