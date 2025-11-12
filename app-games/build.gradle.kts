import com.android.build.api.dsl.BaseFlavor
import java.text.SimpleDateFormat
import java.util.Date

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.android.module)
  alias(libs.plugins.composable)
  alias(libs.plugins.hilt)
  alias(libs.plugins.ksp)
  alias(libs.plugins.gms)
  alias(libs.plugins.crashlytics)
  alias(libs.plugins.tests)
}

android {
  namespace = "com.aptoide.android.aptoidegames"

  ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
  }

  defaultConfig {
    applicationId = "com.aptoide.android.aptoidegames"
    versionCode = Integer.parseInt(project.property("VERSION_CODE_APTOIDEGAMES").toString())
    versionName = (System.getenv("VERSION_NAME") ?: "").ifBlank { "internal.${getDate()}" }

    System.getenv("STORE_NAME")
      .also {
        buildConfigField(
          type = "String",
          name = "MARKET_NAME",
          value = "\"${it ?: "aptoide-games"}\""
        )
      }

    buildConfigField("String", "STORE_DOMAIN", "\"https://ws75-cache.aptoide.com/api/7.20240701/\"")
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
      name = "GAME_GENIE_API",
      value = "\"https://genie-chatbot.aptoide.com/\""
    )

    buildConfigField(
      type = "String",
      name = "RTB_HOST",
      value = "\"https://aptoide-rtb.aptoide.com\""
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
      name = "PP_URL",
      value = "\"https://aptoide.com/legal?section=privacy\""
    )

    buildConfigField(
      type = "String",
      name = "OEMID",
      value = "\"${project.properties[System.getenv("OEMID") ?: KeyHelper.OEMID]}\""
    )

    buildConfigField(
      type = "String",
      name = "MINTEGRAL_APP_KEY",
      value = "\"${project.property("MINTEGRAL_APP_KEY")}\""
    )

    buildConfigFieldFromGradleProperty("ROOM_SCHEMA_VERSION")
    buildConfigFieldFromGradleProperty("ROOM_DATABASE_NAME")
  }

  signingConfigs {
    create("signingConfigRelease") {
      storeFile = project.file(
        project.properties[
          System.getenv("KEY_STORE_FILE") ?: KeyHelper.KEY_STORE_FILE
        ].toString()
      )
      storePassword = project.properties[
        System.getenv("KEY_STORE_PASS") ?: KeyHelper.KEY_STORE_PASS
      ].toString()
      keyAlias =
        project.properties[System.getenv("KEY_ALIAS") ?: KeyHelper.KEY_ALIAS].toString()
      keyPassword =
        project.properties[System.getenv("KEY_PASS") ?: KeyHelper.KEY_PASS].toString()
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
      buildConfigField(
        type = "String",
        name = "AHAB_DOMAIN",
        value = "\"https://api.dev.aptoide.com/ahab/8.20240801/\""
      )
      buildConfigField(
        "String",
        "STORE_ENV_DOMAIN",
        "\"https://ws75-devel.aptoide.com/api/7.20240701/\""
      )
      buildConfigField(
        type = "String",
        name = "MINTEGRAL_APP_ID",
        value = "\"${project.property("MINTEGRAL_APP_ID_DEV")}\""
      )
      buildConfigField(
        type = "String",
        name = "NATIVE_PLACEMENT_ID",
        value = "\"${project.property("NATIVE_PLACEMENT_ID_DEV")}\""
      )
      buildConfigField(
        type = "String",
        name = "NATIVE_UNIT_ID",
        value = "\"${project.property("NATIVE_UNIT_ID_DEV")}\""
      )

      buildConfigField(
        type = "String",
        name = "API_CHAIN_CATAPPULT_HOST",
        value = "\"${project.property("API_CHAIN_CATAPPULT_HOST_DEV")}\""
      )

      buildConfigField(
        type = "String",
        name = "REWARDS_HOST",
        value = "\"${project.property("REWARDS_HOST_DEV")}\""
      )

      buildConfigField(
        type = "String",
        name = "APTOIDE_WEB_SERVICES_HOST",
        value = "\"${project.property("APTOIDE_WEB_SERVICES_HOST_DEV")}\""
      )
      buildConfigField(
        type = "String",
        name = "GOOGLE_AUTH_CLIENT_ID",
        value = "\"${project.property("GOOGLE_AUTH_CLIENT_ID_DEV")}\""
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
      buildConfigField(
        "String",
        "STORE_ENV_DOMAIN",
        "\"https://ws75.aptoide.com/api/7.20240701/\""
      )
      buildConfigField(
        type = "String",
        name = "AHAB_DOMAIN",
        value = "\"https://api.aptoide.com/ahab/8.20240801/\""
      )
      buildConfigField(
        type = "String",
        name = "MINTEGRAL_APP_ID",
        value = "\"${project.property("MINTEGRAL_APP_ID_PROD")}\""
      )
      buildConfigField(
        type = "String",
        name = "NATIVE_PLACEMENT_ID",
        value = "\"${project.property("NATIVE_PLACEMENT_ID_PROD")}\""
      )
      buildConfigField(
        type = "String",
        name = "NATIVE_UNIT_ID",
        value = "\"${project.property("NATIVE_UNIT_ID_PROD")}\""
      )
      buildConfigField(
        type = "String",
        name = "API_CHAIN_CATAPPULT_HOST",
        value = "\"${project.property("API_CHAIN_CATAPPULT_HOST")}\""
      )
      buildConfigField(
        type = "String",
        name = "REWARDS_HOST",
        value = "\"${project.property("REWARDS_HOST")}\""
      )
      buildConfigField(
        type = "String",
        name = "APTOIDE_WEB_SERVICES_HOST",
        value = "\"${project.property("APTOIDE_WEB_SERVICES_HOST_PROD")}\""
      )
      buildConfigField(
        type = "String",
        name = "GOOGLE_AUTH_CLIENT_ID",
        value = "\"${project.property("GOOGLE_AUTH_CLIENT_ID_PROD")}\""
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
        val storeName =
          System.getenv("STORE_NAME")?.takeIf { it != "aptoide-games" }?.let { "_$it" } ?: ""
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
  implementation(projects.featureApps)
  implementation(projects.featureHome)
  implementation(projects.featureFlags)
  implementation(projects.featureAppview)
  implementation(projects.featureCategories)
  implementation(projects.featureEditorial)
  implementation(projects.aptoideUi)
  implementation(projects.featureOos)
  implementation(projects.downloadView)
  implementation(projects.aptoideInstaller)
  implementation(projects.aptoideNetwork)
  implementation(projects.featureCampaigns)
  implementation(projects.environmentInfo)
  implementation(projects.extension)
  implementation(projects.installManager)
  implementation(projects.aptoideTaskInfo)
  implementation(projects.networkListener)
  implementation(projects.featureSearch)
  implementation(projects.featureUpdates)
  implementation(projects.androidYoutubePlayer.core)
  implementation(projects.installInfoMapper)
  implementation(projects.featureApkfy)
  implementation(projects.featureBonus)
  implementation(projects.feautureAppComingSoon)
  implementation(projects.playAndEarn.campaigns)
  implementation(projects.playAndEarn.exchange)
  implementation(projects.featureWallet.authorization)
  implementation(projects.featureWallet.walletInfo)
  implementation(projects.featureWallet.gamification)
  implementation(projects.featureWallet.datastore)

  //payments
  implementation(projects.payments.sdk)
  implementation(projects.payments.guestWallet)
  implementation(projects.payments.uriHandler)
  implementation(projects.payments.managerCompose)
  implementation(projects.payments.methods.adyenCompose)
  implementation(projects.payments.methods.paypalCompose)

  //room
  implementation(libs.room)
  implementation(libs.lifecycle.service)
  ksp(libs.room.compiler)
  implementation(libs.room.ktx)

  //Firebase
  implementation(platform(libs.firebase.bom))
  implementation(libs.bundles.firebase)

  //Indicative
  implementation(libs.indicative.sdk)

  //Store
  implementation(libs.datastore)

  implementation(libs.play.services.basement)
  implementation(libs.gms.play.services.ads)

  //Accompanist
  implementation(libs.accompanist.webview)
  implementation(libs.accompanist.permissions)

  //Workmanager
  implementation(libs.work.manager)
  implementation(libs.hilt.work)

  implementation(libs.lifecycle.process)

  //Pinch to zoom
  implementation(libs.zoomable)

  //Ads
  implementation(libs.mintegral)

  //animations
  implementation(libs.lottie.compose)

  //Palette (dominant color extraction)
  implementation(libs.palette)

  //Authentication and authorization
  implementation(libs.credentials)
  implementation(libs.credentials.playServices)
  implementation(libs.googleId)
  implementation(libs.play.services.auth)

  implementation(libs.constraintLayout.compose)
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
