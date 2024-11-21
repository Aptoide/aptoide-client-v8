pluginManagement {
  includeBuild("build-logic")
  repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
  }
}

dependencyResolutionManagement {
  //TODO: Replace with FAIL_ON_PROJECT_REPOS and remove repository declarations outside this file
  repositoriesMode = RepositoriesMode.PREFER_SETTINGS
  repositories {
    google()
    mavenCentral()
  }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include(
  ":app",
  ":app-games",
  ":aptoide-installer",
  ":aptoide-task-info",
  ":install-manager",
  ":feature_search",
  ":feature_apps",
  ":feature-home",
  ":feature_updates",
  ":aptoide-network",
  ":feature_appview",
  ":feature_report_app",
  ":feature-flags",
  ":payments:arch",
  ":payments:json",
  ":payments:json:ksp",
  ":payments:network",
  ":payments:manager",
  ":payments:manager-compose",
  ":payments:oem-extractor",
  ":payments:oem-extractor:extractor-jar",
  ":payments:methods:adyen",
  ":payments:methods:adyen-compose",
  ":payments:methods:paypal",
  ":payments:methods:paypal:magnes-aar",
  ":payments:methods:paypal-compose",
  ":payments:guest-wallet",
  ":payments:uri-handler",
  ":payments:sdk",
  ":payments:products",
  ":aptoide-ui",
  ":feature_editorial",
  ":feature-reactions",
  ":download-view",
  ":test",
  ":feature_campaigns",
  ":feature_categories",
  ":android-youtube-player:core",
  ":feature-profile",
  ":feature-settings",
  ":environment-info",
  ":extension",
  ":feature-oos",
  ":install-info-mapper",
  ":network-listener",
  ":feature-appcoins",
  ":feature-apkfy",
  ":feature-bonus"
)
