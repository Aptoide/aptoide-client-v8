import com.android.build.api.dsl.BaseFlavor
import java.text.SimpleDateFormat
import java.util.Date

plugins {
  id(GradlePluginId.ANDROID_APPLICATION)
  id(GradlePluginId.ANDROID_MODULE)
  id(GradlePluginId.COMPOSABLE)
  id(GradlePluginId.HILT)
  id(GradlePluginId.KOTLIN_KSP)
  id(GradlePluginId.GMS_PLUGIN_ID)
  id(GradlePluginId.CRASHLYTICS_ID)
}

android {
  namespace = "com.aptoide.android.aptoidegames"

  defaultConfig {
    applicationId = "com.aptoide.android.aptoidegames"
    versionCode = Integer.parseInt(project.property("VERSION_CODE_APTOIDEGAMES").toString())
    versionName = "0.9.0"

    buildConfigField("String", "MARKET_NAME", "\"aptoide-games\"")
    buildConfigField("String", "STORE_DOMAIN", "\"https://ws2-cache.aptoide.com/api/7.20240701/\"")
    buildConfigField("String", "SEARCH_BUZZ_DOMAIN", "\"https://buzz.aptoide.com:10002\"")
    buildConfigField(
      type = "String",
      name = "APTOIDE_WEB_SERVICES_APICHAIN_BDS_HOST",
      value = "\"https://apichain.blockchainds.com/\""
    )

    buildConfigField(
      type = "String",
      name = "APTOIDE_WEB_SERVICES_MMP_HOST",
      value = "\"https://aptoide-mmp.dev.aptoide.com/api/v1/\""
    )

    buildConfigField(
      type = "String",
      name = "DEEP_LINK_SCHEMA",
      value = "\"ag://\""
    )

    buildConfigField(
      type = "String",
      name = "TC_URL",
      value = "\"https://aptoide.com/legal\""
    )
    buildConfigField(
      type = "String",
      name = "BT_URL",
      value = "\"https://aptoide.com/legal?section=aptoidegamesbilling\""
    )
    buildConfigField(
      type = "String",
      name = "PP_URL",
      value = "\"https://aptoide.com/legal?section=privacy\""
    )

    buildConfigField(
      type = "String",
      name = "OEMID",
      value = "\"${project.property("OEMID_APTOIDEGAMES")}\""
    )

    testInstrumentationRunner = AndroidConfig.TEST_INSTRUMENTATION_RUNNER

    buildConfigFieldFromGradleProperty("ROOM_SCHEMA_VERSION")
    buildConfigFieldFromGradleProperty("ROOM_DATABASE_NAME")

    ksp {
      arg("room.schemaLocation", "$projectDir/schemas")
    }

  }

  signingConfigs {
    create("signingConfigRelease") {
      storeFile = project.file(project.properties[KeyHelper.KEY_STORE_FILE].toString())
      storePassword = project.properties[KeyHelper.KEY_STORE_PASS].toString()
      keyAlias = project.properties[KeyHelper.KEY_ALIAS].toString()
      keyPassword = project.properties[KeyHelper.KEY_PASS].toString()
    }
  }

  buildTypes {
    release {
      signingConfig = signingConfigs.getByName("signingConfigRelease")
    }
  }

  flavorDimensions.add(0, "mode")

  productFlavors {
    create("dev") {
      val adyenKey = project.property("ADYEN_PUBLIC_KEY_DEV").toString()
      dimension = "mode"
      applicationIdSuffix = ".dev"
      versionName = "0.9.0"

      manifestPlaceholders["payment_intent_filter_priority"] = "8"
      manifestPlaceholders["payment_intent_filter_host"] =
        project.property("PAYMENT_DEEPLINK_HOST_DEV").toString()

      buildConfigField(
        type = "com.appcoins.payments.arch.Environment",
        name = "PAYMENTS_ENVIRONMENT",
        value = "com.appcoins.payments.arch.Environment.DEV"
      )
      buildConfigField(
        type = "String",
        name = "ADYEN_KEY",
        value = adyenKey
      )
      buildConfigField(
        type = "String",
        name = "INDICATIVE_KEY",
        value = "\"${project.property("INDICATIVE_KEY_DEV")}\""
      )
    }

    create("prod") {
      val adyenKey = project.property("ADYEN_PUBLIC_KEY").toString()
      dimension = "mode"

      manifestPlaceholders["payment_intent_filter_priority"] = "7"
      manifestPlaceholders["payment_intent_filter_host"] =
        project.property("PAYMENT_DEEPLINK_HOST").toString()

      buildConfigField(
        type = "com.appcoins.payments.arch.Environment",
        name = "PAYMENTS_ENVIRONMENT",
        value = "com.appcoins.payments.arch.Environment.PROD"
      )
      buildConfigField(
        type = "String",
        name = "ADYEN_KEY",
        value = adyenKey
      )
      buildConfigField(
        type = "String",
        name = "INDICATIVE_KEY",
        value = "\"${project.property("INDICATIVE_KEY_PROD")}\""
      )

      buildConfigField(
        type = "String",
        name = "APTOIDE_WEB_SERVICES_MMP_HOST",
        value = "\"https://aptoide-mmp.aptoide.com/api/v1/\""
      )
    }
  }

  applicationVariants.all {
    val variant = this
    variant.outputs
      .map { it as com.android.build.gradle.internal.api.BaseVariantOutputImpl }
      .forEach { output ->
        val isSigned = if (variant.isSigningReady) {
          ""
        } else {
          "_unsigned"
        }
        val storeName = System.getenv("STORE_NAME")?.let { "_$it" } ?: ""
        val outputFileName =
          "AptoideGames_${variant.baseName}_${variant.versionName}_${variant.versionCode}$storeName$isSigned.apk"
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
  implementation(project(ModuleDependency.FEATURE_FLAGS))
  implementation(project(ModuleDependency.FEATURE_APPVIEW))
  implementation(project(ModuleDependency.FEATURE_CATEGORIES))
  implementation(project(ModuleDependency.FEATURE_EDITORIAL))
  implementation(project(ModuleDependency.APTOIDE_UI))
  implementation(project(ModuleDependency.FEATURE_OOS))
  implementation(project(ModuleDependency.DOWNLOAD_VIEW))
  implementation(project(ModuleDependency.APTOIDE_INSTALLER))
  implementation(project(ModuleDependency.APTOIDE_NETWORK))
  implementation(project(ModuleDependency.FEATURE_CAMPAIGNS))
  implementation(project(ModuleDependency.ENVIRONMENT_INFO))
  implementation(project(ModuleDependency.EXTENSIONS))
  implementation(project(ModuleDependency.INSTALL_MANAGER))
  implementation(project(ModuleDependency.APTOIDE_TASK_INFO))
  implementation(project(ModuleDependency.NETWORK_LISTENER))
  implementation(project(ModuleDependency.FEATURE_SEARCH))
  implementation(project(ModuleDependency.YOUTUBE_VIDEO_PLAYER))
  implementation(project(ModuleDependency.INSTALL_INFO_MAPPER))
  implementation(project(ModuleDependency.FEATURE_APKFY))

  //payments
  implementation(project(ModuleDependency.PAYMENTS_SDK))
  implementation(project(ModuleDependency.PAYMENTS_GUEST_WALLET))
  implementation(project(ModuleDependency.PAYMENTS_URI_HANDLER))
  implementation(project(ModuleDependency.PAYMENTS_MANAGER_COMPOSE))
  implementation(project(ModuleDependency.PAYMENTS_METHODS_ADYEN_COMPOSE))
  implementation(project(ModuleDependency.PAYMENTS_METHODS_PAYPAL_COMPOSE))

  //room
  implementation(LibraryDependency.ROOM)
  ksp(LibraryDependency.ROOM_COMPILER)
  implementation(LibraryDependency.ROOM_KTX)

  //Firebase
  implementation(platform(LibraryDependency.FIREBASE_BOM))
  implementation(LibraryDependency.FIREBASE_ANALYTICS)
  implementation(LibraryDependency.FIREBASE_CRASHLYTICS)
  implementation(LibraryDependency.FIREBASE_MESSAGING)

  //Indicative
  implementation(LibraryDependency.INDICATIVE_SDK)

  //Store
  implementation(LibraryDependency.DATASTORE)

  implementation(LibraryDependency.PLAY_SERVICES_BASEMENT)
  implementation(LibraryDependency.GMS_PLAY_SERVICES_ADS)

  //Accompanist
  implementation(LibraryDependency.ACCOMPANIST_WEBVIEW)
  implementation(LibraryDependency.ACCOMPANIST_PERMISSIONS)

}

fun BaseFlavor.buildConfigFieldFromGradleProperty(gradlePropertyName: String) {
  val propertyValue = project.properties[gradlePropertyName].toString()
  val androidResourceName = "GRADLE_${gradlePropertyName}"
  buildConfigField("String", androidResourceName, "\"$propertyValue\"")
}

fun getDate(): String {
  val sdf = SimpleDateFormat("yyyyMMdd")
  return sdf.format(Date())
}
