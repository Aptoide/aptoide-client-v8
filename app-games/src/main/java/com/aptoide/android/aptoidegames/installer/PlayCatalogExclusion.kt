package com.aptoide.android.aptoidegames.installer

import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.isInCatappult
import cm.aptoide.pt.feature_apps.domain.AppOrigin
import com.aptoide.android.aptoidegames.apkfy.isFreeFire
import com.aptoide.android.aptoidegames.apkfy.isRoblox

/**
 * Apps that must always install through Aptoide, never Google Play - the builds that carry
 * AppCoins billing, which the Play build would lose. Evaluated before any catalog token is
 * fetched, so neither the "Google Play" tag nor the inline install can apply to them, and the
 * backend handing out a token for the package changes nothing.
 *
 * What tells such an app depends on where it was read from, see [installsThroughAptoide].
 * Roblox and Free Fire are never excluded: on this build they install exclusively through
 * Play's details overlay and may not fall back to Aptoide, so the overlay rule has to win here
 * regardless of which gate asks first - otherwise the label could disappear while the tap
 * still went to Play.
 *
 * Only meaningful for backend-mapped Apps. Synthetic ones built from `emptyApp.copy(...)` (RTB,
 * out-of-space items) carry no flags and read as not excluded; none of them offers an install
 * button today, and any that gains one must source real metadata first.
 */
fun App.excludedFromPlayCatalog(): Boolean =
  installsThroughAptoide() && !isFreeFire() && !isRoblox()

// A v7 app is told by its STORE_BDS flag alone, matched exactly; its billing flag says nothing
// about where it installs. A device API app carries no store flags, only the billing flag.
private fun App.installsThroughAptoide(): Boolean = when (origin) {
  AppOrigin.V7 -> isInCatappult() == true
  AppOrigin.DEVICE_API -> isAppCoins
}
