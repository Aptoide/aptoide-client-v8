package com.aptoide.android.aptoidegames.analytics.dto

import android.net.Uri.encode
import androidx.annotation.Keep
import java.nio.charset.StandardCharsets

@Keep
data class AnalyticsUIContext(
  var currentScreen: String,
  val previousScreen: String?,
  val bundleMeta: BundleMeta?,
  val searchMeta: SearchMeta?,
  val itemPosition: Int?,
  val isApkfy: Boolean,
) {
  companion object {
    val Empty = AnalyticsUIContext(
      currentScreen = "",
      previousScreen = null,
      bundleMeta = null,
      searchMeta = null,
      itemPosition = null,
      isApkfy = false
    )
  }
}

@Keep
data class AnalyticsPayload(
  val isApkfy: Boolean,
  val isAab: Boolean,
  val aabTypes: String,
  val isAppCoins: Boolean,
  val isInCatappult: Boolean?,
  val hasObb: Boolean,
  val versionCode: Int,
  val context: String,
  val previousContext: String?,
  val store: String,
  val bundleMeta: BundleMeta?,
  val searchMeta: SearchMeta?,
  val itemPosition: Int?,
  val trustedBadge: String?,
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
  val searchTerm get() = searchKeyword
  val searchTermSource get() = searchType

  override fun toString(): String = encode(
    "$insertedKeyword~$searchKeyword~$searchType",
    StandardCharsets.UTF_8.toString()
  )

  companion object {
    fun fromString(source: String) = source.split("~").let {
      SearchMeta(
        insertedKeyword = it[0],
        searchKeyword = it[1],
        searchType = it[2]
      )
    }
  }
}
