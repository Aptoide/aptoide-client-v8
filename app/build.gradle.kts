import com.android.build.api.dsl.BaseFlavor
import com.android.build.api.dsl.DefaultConfig
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

plugins {
  id(GradlePluginId.ANDROID_APPLICATION)
  id(GradlePluginId.KOTLIN_ANDROID)
  id(GradlePluginId.KOTLIN_ANDROID_EXTENSIONS)
  id(GradlePluginId.KOTLIN_KAPT)
  id(GradlePluginId.HILT_PLUGIN)
}

android {
  compileSdk = AndroidConfig.COMPILE_SDK

  defaultConfig {
    buildToolsVersion = AndroidConfig.BUILD_TOOLS
    minSdk = AndroidConfig.MIN_SDK
    targetSdk = AndroidConfig.TARGET_SDK
    applicationId = AndroidConfig.ID
    versionCode = AndroidConfig.VERSION_CODE
    versionName = AndroidConfig.VERSION_NAME

    testInstrumentationRunner = AndroidConfig.TEST_INSTRUMENTATION_RUNNER

    manifestPlaceholders["dataPlaceholder"] = generateData()

    buildConfigFieldFromGradleProperty("ROOM_SCHEMA_VERSION")
    buildConfigFieldFromGradleProperty("ROOM_DATABASE_NAME")
  }

  buildFeatures {
    // Enables Jetpack Compose for this module
    compose = true
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

  // Set both the Java and Kotlin compilers to target Java 8.
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }
  kotlinOptions {
    jvmTarget = JavaVersion.VERSION_1_8.toString()
  }

  composeOptions {
    kotlinCompilerExtensionVersion = CoreVersion.KT_COMPILER_EXTENSION
  }

  flavorDimensions.add("mode")

  productFlavors {
    create("dev") {
      dimension = "mode"
      applicationIdSuffix = ".dev"
      versionName = AndroidConfig.VERSION_NAME + "." + getDate()
      versionCode = AndroidConfig.VERSION_CODE
    }

    create("prod") {
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
          val manifestDir = "app/build/intermediates/merged_manifests/$buildType"
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
  implementation(project(ModuleDependency.FEATURE_SEARCH))
  implementation(project(ModuleDependency.FEATURE_UPDATES))
  implementation(project(ModuleDependency.FEATURE_PROFILE))
  implementation(project(ModuleDependency.FEATURE_SETTINGS))
  implementation(project(ModuleDependency.INSTALLED_APPS))
  implementation(project(ModuleDependency.APTOIDE_NETWORK))
  implementation(project(ModuleDependency.FEATURE_APPVIEW))
  implementation(project(ModuleDependency.APTOIDE_UI))

  implementation(LibraryDependency.CORE_KTX)
  implementation(LibraryDependency.APP_COMPAT)
  implementation(LibraryDependency.MATERIAL)
  implementation(LibraryDependency.CONSTRAINT_LAYOUT)
  implementation(LibraryDependency.KOTLIN)
  implementation(LibraryDependency.COROUTINES)
  testImplementation(TestLibraryDependency.COROUTINES_TEST)
  //implementation(LibraryDependency.FRAGMENT_KTX)
  implementation(LibraryDependency.LIFECYCLE_EXTENSIONS)
  implementation(LibraryDependency.LIFECYCLE_VIEW_MODEL_KTX)
  implementation(LibraryDependency.NAVIGATION_FRAGMENT_KTX)
  implementation(LibraryDependency.NAVIGATION_UI_KTX)
  implementation(LibraryDependency.LOTTIE)
  implementation(LibraryDependency.ROOM)
  kapt(LibraryDependency.ROOM_COMPILER)
  implementation(LibraryDependency.ROOM_KTX)
  androidTestImplementation(TestLibraryDependency.ROOM_TESTING)
  testImplementation(TestLibraryDependency.JUNIT)
  androidTestImplementation(TestLibraryDependency.JUNIT_ANDROIDX)

  //imageloader
  implementation(LibraryDependency.COIL)
  implementation(LibraryDependency.COIL_COMPOSE)

  //compose-ui
  implementation(LibraryDependency.ACTIVITY_COMPOSE)
  implementation(LibraryDependency.MATERIAL_COMPOSE)
  implementation(LibraryDependency.ANIMATION_COMPOSE)
  implementation(LibraryDependency.UI_TOOLING_COMPOSE)
  implementation(LibraryDependency.VIEWMODEL_COMPOSE)
  implementation(LibraryDependency.NAVIGATION_COMPOSE)

  implementation(LibraryDependency.MATERIAL_ICONS_EXTENDED)

  //network
  implementation(LibraryDependency.RETROFIT)
  implementation(LibraryDependency.RETROFIT_GSON_CONVERTER)
  implementation(LibraryDependency.OK_HTTP)
  implementation(LibraryDependency.LOGGING_INTERCEPTOR)

  //di
  implementation(LibraryDependency.HILT)
  implementation(LibraryDependency.HILT_NAV_COMPOSE)
  kapt(LibraryDependency.HILT_COMPILER)

  //logger
  implementation(LibraryDependency.TIMBER)
  implementation("androidx.datastore:datastore-preferences:1.0.0")

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