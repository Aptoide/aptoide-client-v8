import org.gradle.kotlin.dsl.get

val compileSdkVersion by extra { libs.versions.compileSdk.get() }
val minSdkVersion by extra { libs.versions.minSdk.get() }

// Billing SDK
val supportedSdkVersion by extra { "3" }

// Kotlin
val coreKtxVersion by rootProject.extra { libs.versions.coreKtx.get() }
val kotlinxCoroutinesAndroidVersion by rootProject.extra { libs.versions.coroutines.get() }

// KSP
val kspVersion by extra { libs.versions.kspPlugin.get() }

// Hilt
val hiltAndroidVersion by rootProject.extra { libs.versions.hilt.get() }
val daggerHiltCompilerVersion by rootProject.extra { libs.versions.hilt.get() }

// Apache
val apacheCommonsTextVersion by rootProject.extra { libs.versions.apacheCommonsText.get() }

// Android Material
val androidxActivityVersion by rootProject.extra { libs.versions.activityKtx.get() }

// Adyen
val adyenVersion by rootProject.extra { libs.versions.adyenVersion.get() }

// Compose
val navigationComposeVersion by rootProject.extra { libs.versions.navigationCompose.get() }
val hiltNavigationComposeVersion by rootProject.extra { libs.versions.hiltNavCompose.get() }
val composeMaterialVersion by rootProject.extra { libs.versions.compose.get() }
val lifecycleViewModelComposeVersion by rootProject.extra { libs.versions.viewmodelCompose.get() }

buildscript {

  repositories {
    google()
    mavenCentral()
  }
  dependencies {
    classpath(libs.android.gradle.plugin)
    classpath(libs.kotlin.gradle.plugin)
    classpath(libs.hilt.android.gradle.plugin)
    classpath(libs.android.junit5.plugin)
    classpath(libs.google.services.plugin)
    classpath(libs.firebase.crashlytics.gradle)
    classpath(libs.ksp.plugin)
    classpath(libs.compose.gradle.plugin)
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
