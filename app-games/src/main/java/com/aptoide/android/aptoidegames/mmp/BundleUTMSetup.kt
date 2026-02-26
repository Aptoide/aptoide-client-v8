package com.aptoide.android.aptoidegames.mmp

import cm.aptoide.pt.feature_campaigns.UTMInfo

private const val MORE_SUFFIX = "-more"
private const val APPS_GROUP_PREFIX = "apps-group-"

fun getBundleHomeUTMInfo(tag: String): UTMInfo {
  return when (val baseTag = tag.removeSuffix(MORE_SUFFIX)) {
    "apps-group-just-arrived" -> UTMInfo(utmContent = "home-just-arrived")
    "apps-group-trending" -> UTMInfo(utmContent = "home-trending")
    "apps-group-editors-choice" -> UTMInfo(utmContent = "home-editors-choice")
    "apps-group-featured-appcoins" -> UTMInfo(utmContent = "home-get-rewarded")
    "new-app" -> UTMInfo(utmContent = "home-new-app")
    else -> if (baseTag.startsWith(APPS_GROUP_PREFIX)) {
      val suffix = baseTag.removePrefix(APPS_GROUP_PREFIX)
      UTMInfo(utmContent = "home-$suffix")
    } else {
      UTMInfo()
    }
  }
}

fun getBundleSeeAllUTMInfo(tag: String): UTMInfo {
  return when (val baseTag = tag.removeSuffix(MORE_SUFFIX)) {
    "apps-group-just-arrived" -> UTMInfo(utmContent = "just-arrived-seeall")
    "apps-group-trending" -> UTMInfo(utmContent = "trending-seeall")
    "apps-group-editors-choice" -> UTMInfo(utmContent = "editors-choice-seeall")
    "apps-group-featured-appcoins" -> UTMInfo(utmContent = "get-rewarded-seeall")
    else -> if (baseTag.startsWith(APPS_GROUP_PREFIX)) {
      val prefix = baseTag.removePrefix(APPS_GROUP_PREFIX)
      UTMInfo(utmContent = "$prefix-seeall")
    } else {
      UTMInfo()
    }
  }
}
