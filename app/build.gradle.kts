import com.android.build.api.dsl.BaseFlavor
import com.android.build.api.dsl.DefaultConfig
import java.text.SimpleDateFormat
import java.util.Date

plugins {
  id(GradlePluginId.ANDROID_APPLICATION)
  id(GradlePluginId.ANDROID_MODULE)
  id(GradlePluginId.COMPOSABLE)
  id(GradlePluginId.HILT)
}

android {
  namespace = "cm.aptoide.pt"

  defaultConfig {
    applicationId = AndroidConfig.ID
    versionCode = AndroidConfig.VERSION_CODE
    versionName = AndroidConfig.VERSION_NAME

    buildConfigField("String", "MARKET_NAME", "\"apps\"")
    buildConfigField("String", "STORE_DOMAIN", "\"https://ws75.aptoide.com/api/7.20221201/\"")
    buildConfigField("String", "SEARCH_BUZZ_DOMAIN", "\"https://buzz.aptoide.com:10002\"")
    buildConfigField(
      type = "String",
      name = "APTOIDE_WEB_SERVICES_APICHAIN_BDS_HOST",
      value = "\"https://apichain.blockchainds.com/\""
    )

    testInstrumentationRunner = AndroidConfig.TEST_INSTRUMENTATION_RUNNER

    buildConfigFieldFromGradleProperty("ROOM_SCHEMA_VERSION")
    buildConfigFieldFromGradleProperty("ROOM_DATABASE_NAME")
  }

  signingConfigs {
    create("signingConfigRelease") {
      storeFile = file(project.properties[KeyHelper.KEY_STORE_FILE].toString())
      storePassword = project.properties[KeyHelper.KEY_STORE_PASS].toString()
      keyAlias = project.properties[KeyHelper.KEY_ALIAS].toString()
      keyPassword = project.properties[KeyHelper.KEY_PASS].toString()
    }
  }

  flavorDimensions.add(0, "mode")

  productFlavors {
    create("dev") {
      dimension = "mode"
      applicationIdSuffix = ".dev"
      versionName = AndroidConfig.VERSION_NAME + "." + getDate()
      versionCode = AndroidConfig.VERSION_CODE
    }

    create("prod") {
      signingConfig = signingConfigs.getByName("signingConfigRelease")
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

  hilt {
    enableAggregatingTask = true
  }
}
dependencies {
  implementation(project(ModuleDependency.FEATURE_APPS))
  implementation(project(ModuleDependency.FEATURE_HOME))
  implementation(project(ModuleDependency.FEATURE_SEARCH))
  implementation(project(ModuleDependency.FEATURE_UPDATES))
  implementation(project(ModuleDependency.APTOIDE_NETWORK))
  implementation(project(ModuleDependency.FEATURE_CATEGORIES))
  implementation(project(ModuleDependency.FEATURE_APPVIEW))
  implementation(project(ModuleDependency.FEATURE_FLAGS))
  implementation(project(ModuleDependency.APTOIDE_UI))
  implementation(project(ModuleDependency.DOWNLOAD_VIEW))
  implementation(project(ModuleDependency.APTOIDE_INSTALLER))
  implementation(project(ModuleDependency.APTOIDE_TASK_INFO))
  implementation(project(ModuleDependency.NETWORK_LISTENER))
  implementation(project(ModuleDependency.FEATURE_PROFILE))
  implementation(project(ModuleDependency.FEATURE_EDITORIAL))
  implementation(project(ModuleDependency.FEATURE_REACTIONS))
  implementation(project(ModuleDependency.FEATURE_SETTINGS))
  implementation(project(ModuleDependency.FEATURE_OOS))
  implementation(project(ModuleDependency.ENVIRONMENT_INFO))
  implementation(project(ModuleDependency.EXTENSIONS))
  implementation(project(ModuleDependency.FEATURE_APPCOINS))

  implementation(LibraryDependency.CUSTOM_CHROME_TAB)

  //firebase
  implementation(LibraryDependency.FIREBASE_ANALYTICS)
  implementation(platform(LibraryDependency.FIREBASE_BOM))

  //store
  implementation(LibraryDependency.DATASTORE)

  // google play service
  implementation(LibraryDependency.PLAY_SERVICES_BASEMENT)

  //Accompanist
  implementation(LibraryDependency.ACCOMPANIST_WEBVIEW)
}


fun BaseFlavor.buildConfigFieldFromGradleProperty(gradlePropertyName: String) {
  val propertyValue = project.properties[gradlePropertyName].toString()
  val androidResourceName = "GRADLE_${gradlePropertyName}"
  buildConfigField("String", androidResourceName, "\"$propertyValue\"")
}

fun DefaultConfig.buildConfigField(name: String, value: String) {
  buildConfigField("String", name, "\"$value\"")
}

fun DefaultConfig.buildConfigField(name: String, value: Int) {
  buildConfigField("Int", name, value.toString())
}

fun DefaultConfig.buildConfigField(name: String, value: Boolean) {
  buildConfigField("Boolean", name, value.toString())
}

fun getDate(): String {
  val sdf = SimpleDateFormat("yyyyMMdd")
  return sdf.format(Date())
}
