package com.aptoide.android.aptoidegames.installer.analytics

import androidx.annotation.Keep
import cm.aptoide.pt.feature_apps.domain.AppOrigin
import com.aptoide.android.aptoidegames.analytics.dto.AnalyticsUIContext
import com.aptoide.android.aptoidegames.analytics.dto.BundleMeta
import com.aptoide.android.aptoidegames.analytics.dto.InstallAction
import com.aptoide.android.aptoidegames.analytics.dto.SearchMeta

@Keep
data class AnalyticsPayload(
  val action: InstallAction?,
  val isApkfy: Boolean,
  val homeTab: String?,
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
  val userClicks: Int,
  // Absent from payloads stored before it existed, which read as null
  val catalogSource: String? = null,
)

fun AnalyticsPayload.toAnalyticsUiContext(): AnalyticsUIContext = AnalyticsUIContext(
  isApkfy = isApkfy,
  homeTab = homeTab,
  currentScreen = context,
  previousScreen = previousContext,
  bundleMeta = bundleMeta,
  searchMeta = searchMeta,
  itemPosition = itemPosition,
  installAction = action
)

/** The catalog an app was read from, as the install analytics report it. */
val AppOrigin.catalogSource: String
  get() = when (this) {
    AppOrigin.V7 -> "v7"
    AppOrigin.DEVICE_API -> "device_api"
  }
