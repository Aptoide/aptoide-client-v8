package com.aptoide.android.aptoidegames.mmp

data class UTMConfig(
  val homeContent: String,
  val seeAllContent: String?
)

private const val MORE_SUFFIX = "-more"
private const val APPS_GROUP_PREFIX = "apps-group-"

fun getUTMConfig(bundleTag: String): UTMConfig? {
  val baseTag = bundleTag.removeSuffix(MORE_SUFFIX)
  return when (baseTag) {
    "apps-group-just-arrived" -> UTMConfig("home-just-arrived", "just-arrived-seeall")
    "apps-group-trending" -> UTMConfig("home-trending", "trending-seeall")
    "apps-group-editors-choice" -> UTMConfig("home-editors-choice", "editors-choice-seeall")
    "apps-group-featured-appcoins" -> UTMConfig("home-get-rewarded", "get-rewarded-seeall")
    "new-app" -> UTMConfig("home-new-app", null)
    else -> {
      if (baseTag.startsWith(APPS_GROUP_PREFIX)) {
        val suffix = baseTag.removePrefix(APPS_GROUP_PREFIX)
        UTMConfig("home-$suffix", "$suffix-seeall")
      } else {
        null
      }
    }
  }
}
