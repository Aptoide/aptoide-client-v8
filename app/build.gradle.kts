plugins {
  id(GradlePluginId.ANDROID_APPLICATION)
  id(GradlePluginId.KOTLIN_ANDROID)
  id(GradlePluginId.KOTLIN_ANDROID_EXTENSIONS)
  id(GradlePluginId.KOTLIN_KAPT)
}

/*buildscript {
  repositories {
    maven(
        url = "https://maven.google.com"
    )
  }
}*/


android {
  compileSdkVersion(AndroidConfig.COMPILE_SDK)

  defaultConfig {
    buildToolsVersion(AndroidConfig.BUILD_TOOLS)
    minSdkVersion(AndroidConfig.MIN_SDK)
    targetSdkVersion(AndroidConfig.TARGET_SDK)
    applicationId = AndroidConfig.ID
    versionCode = AndroidConfig.VERSION_CODE
    versionName = AndroidConfig.VERSION_NAME

    testInstrumentationRunner = AndroidConfig.TEST_INSTRUMENTATION_RUNNER
  }

  signingConfigs {
    create("signingConfigRelease") {
      storeFile = file(project.properties[KeyHelper.KEY_STORE_FILE].toString())
      storePassword = project.properties[KeyHelper.KEY_STORE_PASS].toString()
      keyAlias = project.properties[KeyHelper.KEY_ALIAS].toString()
      keyPassword = project.properties[KeyHelper.KEY_PASS].toString()
      enableV2Signing = false
    }
  }

  buildTypes {
    getByName(BuildType.RELEASE) {
      isMinifyEnabled = BuildTypeRelease.isMinifyEnabled
      isShrinkResources = BuildTypeRelease.shrinkResources
      proguardFiles("proguard-android.txt", "proguard-rules.pro")
      signingConfig = signingConfigs.getByName("signingConfigRelease")
    }

    getByName(BuildType.DEBUG) {
      isMinifyEnabled = BuildTypeDebug.isMinifyEnabled
      isShrinkResources = BuildTypeDebug.shrinkResources
    }

  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }
  kotlinOptions {
    jvmTarget = JavaVersion.VERSION_1_8.toString()
  }

  flavorDimensions("mode")
  productFlavors {
    create("dev") {
      dimension = "mode"
      applicationIdSuffix = "dev"
      versionName = AndroidConfig.VERSION_NAME + "."
      versionCode = AndroidConfig.VERSION_CODE
    }

    create("prod") {
      dimension = "mode"
    }
  }

  applicationVariants.all {
    val variant = this
    variant.outputs
        .map { it as com.android.build.gradle.internal.api.BaseVariantOutputImpl }
        .forEach { output ->
          val outputFileName =
              "vanilla_${variant.baseName}_${variant.versionName}_${variant.versionCode}.apk"
          println("OutputFileName: $outputFileName")
          output.outputFileName = outputFileName
        }
  }

}

/*
fun getDate(): String(){
  return Instant.now().truncatedTo(ChronoUnit.DAYS).toString()
}
*/

dependencies {

  implementation("androidx.core:core-ktx:1.2.0")
  implementation("androidx.appcompat:appcompat:1.3.0")
  implementation("com.google.android.material:material:1.4.0")
  implementation("androidx.constraintlayout:constraintlayout:2.1.2")
  testImplementation("junit:junit:4.+")
  androidTestImplementation("androidx.test.ext:junit:1.1.2")
  androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
  //lottie
  //retrofit
  //google
  //coroutines

}