import com.android.build.api.dsl.BaseFlavor
import com.android.build.api.dsl.DefaultConfig
import java.text.SimpleDateFormat
import java.util.Date
import java.util.regex.Pattern

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
    versionCode = AndroidConfig.VERSION_CODE
    versionName = AndroidConfig.VERSION_NAME

    buildConfigField("String", "MARKET_NAME", "\"aptoide-games\"")
    buildConfigField("String", "STORE_DOMAIN", "\"https://ws75.aptoide.com/api/7.20221201/\"")
    buildConfigField("String", "SEARCH_BUZZ_DOMAIN", "\"https://buzz.aptoide.com:10002\"")
    buildConfigField(
      type = "String",
      name = "APTOIDE_WEB_SERVICES_APICHAIN_BDS_HOST",
      value = "\"https://apichain.blockchainds.com/\""
    )

    buildConfigField(
      type = "String",
      name = "DEEP_LINK_SCHEMA",
      value = "\"ag://\""
    )

    buildConfigField(
      type = "String",
      name = "TC_URL",
      value = "\"https://en.aptoide.com/company/legal\""
    )
    buildConfigField(
      type = "String",
      name = "PP_URL",
      value = "\"https://en.aptoide.com/company/legal?section=privacy\""
    )

    testInstrumentationRunner = AndroidConfig.TEST_INSTRUMENTATION_RUNNER

    manifestPlaceholders["dataPlaceholder"] = generateData()

    buildConfigFieldFromGradleProperty("ROOM_SCHEMA_VERSION")
    buildConfigFieldFromGradleProperty("ROOM_DATABASE_NAME")

    ksp {
      arg("room.schemaLocation", "$projectDir/schemas")
    }

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
    variant.mergedFlavor.manifestPlaceholders["dataPlaceholder"] = generateData()

    variant.outputs.forEach { output ->
      output.processManifestProvider.get().doLast {
        val placeholders = variant.mergedFlavor.manifestPlaceholders
        if (placeholders.isNotEmpty()) {
          val buildType = "${variant.flavorName}${variant.buildType.name.capitalize()}"
          val manifestDir = "app-games/build/intermediates/merged_manifests/$buildType"
          val manifestFilePath = "$manifestDir/AndroidManifest.xml"
          val manifestFile = File(manifestFilePath)

          var manifestContent = manifestFile.readText()
          placeholders.forEach { (key, value) ->
            val pattern = Pattern.compile(Pattern.quote("<!-- \${$key} -->"), Pattern.DOTALL)
            manifestContent = pattern.matcher(manifestContent).replaceAll(value as String?)
          }
          manifestFile.writeText(manifestContent)
        }
      }

    }

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
          "AppGames_${variant.baseName}_${variant.versionName}_${variant.versionCode}$storeName$isSigned.apk"
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

  //room
  implementation(LibraryDependency.ROOM)
  ksp(LibraryDependency.ROOM_COMPILER)
  implementation(LibraryDependency.ROOM_KTX)

  //Firebase
  implementation(platform(LibraryDependency.FIREBASE_BOM))
  implementation(LibraryDependency.FIREBASE_ANALYTICS)
  implementation(LibraryDependency.FIREBASE_CRASHLYTICS)
  implementation(LibraryDependency.FIREBASE_MESSAGING)


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

fun DefaultConfig.buildConfigField(name: String, value: String) {
  buildConfigField("String", name, "\"$value\"")
}

fun DefaultConfig.buildConfigField(name: String, value: Int) {
  buildConfigField("Int", name, value.toString())
}

fun DefaultConfig.buildConfigField(name: String, value: Boolean) {
  buildConfigField("Boolean", name, value.toString())
}

fun generateData(): String {
  return data("app.aptoide.com") + data("market.android.com") +
    dataWithPathPrefix("webservices.aptoide.com", "/apkinstall") +
    data("play.google.com") +
    data("imgs.aptoide.com", "*//.myapp") +
    aptoideSubdomainDataWithWildCardPrefix() +
    aptoideSubdomainData("/store/..*") +
    aptoideSubdomainData("/thank-you*") +
    data("community.aptoide.com", "/using-appcoins*") +
    aptoideSubdomainData("/download*") +
    aptoideSubdomainData("/editorial/..*") +
    aptoideSubdomainData("/app") +
    aptoideSubdomainData()
}

fun getAptoideSubdomainsList(): ArrayList<String> {
  return arrayListOf(
    "en", "pt", "br", "fr", "es", "mx", "de", "it", "ru", "sa", "id", "in", "bd",
    "mr", "pa",
    "my", "th", "vn", "tr", "cn", "ro", "mm", "pl", "rs", "hu", "gr", "bg", "nl", "ir"
  )
}

fun aptoideSubdomainDataWithWildCardPrefix(): String {
  var subdomainData = ""
  val subdomainList: List<String> = getAptoideSubdomainsList()
  for (subdomain in subdomainList) {
    subdomainData += data("*.$subdomain.aptoide.com")
  }
  return subdomainData
}

fun aptoideSubdomainData(): String {
  var subdomainData = ""
  val subdomainList: List<String> = getAptoideSubdomainsList()
  for (subdomain in subdomainList) {
    subdomainData += data("$subdomain.aptoide.com")
  }
  return subdomainData
}

fun aptoideSubdomainData(pathPattern: String): String {
  var subdomainData = ""
  val subdomainList: List<String> = getAptoideSubdomainsList()
  for (subdomain in subdomainList) {
    subdomainData += data("$subdomain.aptoide.com", pathPattern)
  }
  return subdomainData
}

fun data(host: String, pathPattern: String): String {
  return generateIntentFilter(http(host, pathPattern) + https(host, pathPattern))
}

fun data(host: String): String {
  return generateIntentFilter(http(host, "") + https(host, ""))
}

fun dataWithPathPrefix(host: String, pathPrefix: String): String {
  return generateIntentFilter(
    createDataTagWithPathPrefix(host, "http", pathPrefix) + createDataTagWithPathPrefix(
      host,
      "https", pathPrefix
    )
  )
}

fun http(host: String, pathPattern: String): String {
  return if (pathPattern.isNotEmpty()) {
    createDataTagWithPathPattern(host, "http", pathPattern)
  } else {
    createDataTagWithNoPathPattern(host, "http")
  }
}

fun https(host: String, pathPattern: String): String {
  return if (pathPattern.isNotEmpty()) {
    createDataTagWithPathPattern(host, "https", pathPattern)
  } else {
    createDataTagWithNoPathPattern(host, "https")
  }
}

fun createDataTagWithNoPathPattern(host: String, scheme: String): String {
  return "\n" + "               <data\n" +
    "                   android:host=\"$host\"\n" +
    "                   android:scheme=\"$scheme\"/>\n"
}

fun createDataTagWithPathPattern(
  host: String, scheme: String,
  pathPattern: String
): String {
  return "\n" + "               <data\n" +
    "                   android:host=\"$host\"\n" +
    "                   android:pathPattern=\"$pathPattern\"\n" +
    "                   android:scheme=\"$scheme\"/>\n"
}

fun createDataTagWithPathPrefix(host: String, scheme: String, pathPrefix: String): String {
  return "\n" + "               <data\n" +
    "                   android:host=\"$host\"\n" +
    "                   android:pathPrefix=\"$pathPrefix\"\n" +
    "                   android:scheme=\"$scheme\"/>\n"
}

fun generateIntentFilter(data: String): String {
  return "\n            <intent-filter>\n" +
    "                <action android:name=\"android.intent.action.VIEW\"/>\n" +
    "\n" +
    "                <category android:name=\"android.intent.category.DEFAULT\"/>\n" +
    "                <category android:name=\"android.intent.category.BROWSABLE\"/>\n" +
    "\n" +
    data +
    "            </intent-filter>\n"
}

fun getDate(): String {
  val sdf = SimpleDateFormat("yyyyMMdd")
  return sdf.format(Date())
}
