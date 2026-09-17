package com.aptoide.android.aptoidegames.installer

import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.isInCatappult
import com.aptoide.android.aptoidegames.apkfy.isFreeFire
import com.aptoide.android.aptoidegames.apkfy.isRoblox

/**
 * Apps that must always install through Aptoide, never Google Play - the BDS (Catappult)
 * builds carry AppCoins billing that the Play build would lose. Evaluated before any catalog
 * token is fetched, so neither the "Google Play" tag nor the inline install can apply to them,
 * and the backend handing out a token for the package changes nothing.
 *
 * Keys off the STORE_BDS flag alone, matched exactly; an absent or unrelated flag list keeps the
 * Play path. Roblox and Free Fire are never excluded: on this build they install exclusively
 * through Play's details overlay and may not fall back to Aptoide, so the overlay rule has to
 * win here regardless of which gate asks first - otherwise the label could disappear while the
 * tap still went to Play.
 *
 * Only meaningful for backend-mapped Apps. Synthetic ones built from `emptyApp.copy(...)` (RTB,
 * out-of-space items) carry no flags and read as not excluded; none of them offers an install
 * button today, and any that gains one must source real metadata first.
 */
fun App.excludedFromPlayCatalog(): Boolean =
  isInCatappult() == true && !isFreeFire() && !isRoblox()
