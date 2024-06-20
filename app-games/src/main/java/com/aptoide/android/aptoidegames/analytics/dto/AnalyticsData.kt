package com.aptoide.android.aptoidegames.analytics.dto

import androidx.annotation.Keep
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Keep
data class AnalyticsUIContext(
  val currentScreen: String,
  val previousScreen: String?,
  val bundleMeta: BundleMeta?,
  val searchMeta: SearchMeta?,
  val itemPosition: Int?,
) {
  companion object {
    val Empty = AnalyticsUIContext(
      currentScreen = "",
      previousScreen = null,
      bundleMeta = null,
      searchMeta = null,
      itemPosition = null
    )
  }
}

@Keep
data class AnalyticsPayload(
  val isApkfy: Boolean,
  val isAab: Boolean,
  val aabInstallTime: String,
  val isAppCoins: Boolean,
  val isInCatappult: Boolean,
  val isGame: Boolean,
  val isMigration: Boolean,
  val hasObb: Boolean,
  val versionCode: Int,
  val context: String,
  val previousContext: String?,
  val store: String,
  val bundleMeta: BundleMeta?,
  val searchMeta: SearchMeta?,
  val itemPosition: Int?,
  val trustedBadge: String?,
  val adListId: String?,
  val impressions: List<String>,
  val clicks: List<String>,
)

@Keep
data class BundleMeta(
  val tag: String,
  val bundleSource: String,
) {
  override fun toString(): String = "$tag~$bundleSource"

  companion object {
    fun fromString(source: String) = source.split("~").let { BundleMeta(it[0], it[1]) }
  }
}

@Keep
data class SearchMeta(
  val insertedKeyword: String,
  val searchKeyword: String,
  val searchType: String,
) {
  override fun toString(): String = listOf(
    URLEncoder.encode(insertedKeyword, StandardCharsets.UTF_8.toString()),
    URLEncoder.encode(searchKeyword, StandardCharsets.UTF_8.toString()),
    searchType
  ).joinToString("~")

  companion object {
    fun fromString(source: String) = source.split("~").let {
      SearchMeta(
        insertedKeyword = URLDecoder.decode(it[0], StandardCharsets.UTF_8.toString()),
        searchKeyword = URLDecoder.decode(it[1], StandardCharsets.UTF_8.toString()),
        searchType = it[2]
      )
    }
  }
}
