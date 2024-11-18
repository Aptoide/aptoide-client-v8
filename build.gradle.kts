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
}

plugins {
  alias(libs.plugins.android.application) apply false
  alias(libs.plugins.android.library) apply false
  alias(libs.plugins.ksp) apply false
  alias(libs.plugins.gms) apply false
  alias(libs.plugins.crashlytics) apply false
  alias(libs.plugins.junit5) apply false
  alias(libs.plugins.kotlin.android) apply false
  alias(libs.plugins.kotlin.compose.compiler) apply false
  alias(libs.plugins.hilt.android.plugin) apply false
}

tasks.register("clean", Delete::class) {
  delete(rootProject.layout.buildDirectory)
}
