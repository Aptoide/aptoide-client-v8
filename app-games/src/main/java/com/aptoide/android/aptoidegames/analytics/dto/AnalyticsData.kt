package com.aptoide.android.aptoidegames.analytics.dto

import androidx.annotation.Keep

@Keep
data class AnalyticsUIContext(
  val currentScreen: String,
  val previousScreen: String?,
  val bundleMeta: BundleMeta?,
) {
  companion object {
    val Empty = AnalyticsUIContext("", null, null)
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
