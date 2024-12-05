package com.aptoide.android.aptoidegames.installer.analytics

import androidx.annotation.Keep
import com.aptoide.android.aptoidegames.analytics.dto.AnalyticsUIContext
import com.aptoide.android.aptoidegames.analytics.dto.BundleMeta
import com.aptoide.android.aptoidegames.analytics.dto.SearchMeta

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

fun AnalyticsPayload.toAnalyticsUiContext(): AnalyticsUIContext = AnalyticsUIContext(
  isApkfy = isApkfy,
  currentScreen = context,
  previousScreen = previousContext,
  bundleMeta = bundleMeta,
  searchMeta = searchMeta,
  itemPosition = itemPosition,
)
