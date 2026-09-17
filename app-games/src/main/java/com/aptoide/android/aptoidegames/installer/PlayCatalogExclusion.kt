package com.aptoide.android.aptoidegames.installer

import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.isInCatappult

/**
 * Apps that must always install through Aptoide, never Google Play - the BDS (Catappult)
 * builds carry AppCoins billing that the Play build would lose. Evaluated before any catalog
 * token is fetched, so neither the "Google Play" tag nor the inline install can apply to them,
 * and the backend handing out a token for the package changes nothing.
 *
 * Keys off the STORE_BDS flag alone; an absent or unrelated flag list keeps the Play path.
 */
fun App.excludedFromPlayCatalog(): Boolean = isInCatappult() == true
