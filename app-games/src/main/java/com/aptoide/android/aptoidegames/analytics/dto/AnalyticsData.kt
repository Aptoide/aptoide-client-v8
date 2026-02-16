package com.aptoide.android.aptoidegames.analytics.dto

import android.net.Uri.encode
import androidx.annotation.Keep
import java.nio.charset.StandardCharsets

@Keep
@Suppress("unused")
enum class InstallAction {
  INSTALL,
  UPDATE,
  MIGRATE,
  MIGRATE_ALIAS,
  DOWNGRADE,
  UPDATE_ALL,
  RETRY,
  UNINSTALL
}

@Keep
data class AnalyticsUIContext(
  var currentScreen: String,
  val previousScreen: String?,
  val bundleMeta: BundleMeta?,
  val searchMeta: SearchMeta?,
  val itemPosition: Int?,
  val isApkfy: Boolean,
  val installAction: InstallAction? = null,
  val homeTab: String? = null,
  val isPlayAndEarn: Boolean = false
) {
  companion object {
    val Empty = AnalyticsUIContext(
      currentScreen = "",
      previousScreen = null,
      bundleMeta = null,
      searchMeta = null,
      itemPosition = null,
      isApkfy = false,
      homeTab = null,
      isPlayAndEarn = false
    )
  }
}

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
