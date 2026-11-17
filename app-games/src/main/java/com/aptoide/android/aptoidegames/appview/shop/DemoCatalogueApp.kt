package com.aptoide.android.aptoidegames.appview.shop

import android.content.Context
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.emptyApp
import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.gamegenie.purchase.GeniePurchaseCatalog

// =================================================================================================
// HACKATHON MOCK, DEBUG BUILDS ONLY. The Diceroll dev build (com.aptoide.diceroll.sdk.dev) is not in
// the catalogue the store reads (ws75-cache, store aptoide-games), so its AppView fails to load.
// For an INSTALLED Genie catalogue game, debug builds show an AppView built from these values
// instead of the error, so the Shop tab can be demoed. Icon/graphic URLs are the game's own
// images from the dev webservices (ws75-devel app/get).
// =================================================================================================

private data class DemoAppMeta(val name: String, val icon: String, val featureGraphic: String)

private val demoAppMeta = mapOf(
  "Diceroll" to DemoAppMeta(
    name = "Aptoide Diceroll",
    icon = "https://pool.img.aptoide.com/catappult/fe577e264301129542227385562d682e_icon.webp",
    featureGraphic =
      "https://pool.img.aptoide.com/catappult/b737866e7065a09e02a8393dd4c83a42_fgraphic.webp",
  )
)

private val packageNameInSource = Regex("package_name=([^/&?]+)")

/** The demo App for an AppView [source] like `package_name=<pkg>[/store_name=...]`, or null. */
fun demoCatalogueAppOrNull(context: Context, source: String): App? {
  if (!BuildConfig.DEBUG) return null
  val packageName = packageNameInSource.find(source)?.groupValues?.get(1) ?: return null
  val item = GeniePurchaseCatalog.items.firstOrNull { packageName in it.packageCandidates }
    ?: return null
  val packageInfo = runCatching { context.packageManager.getPackageInfo(packageName, 0) }
    .getOrNull() ?: return null
  val apkSize = runCatching { java.io.File(packageInfo.applicationInfo!!.sourceDir).length() }
    .getOrDefault(0L)
  val meta = demoAppMeta[item.gameName]
  return emptyApp.copy(
    name = meta?.name ?: item.gameName,
    packageName = packageName,
    icon = meta?.icon.orEmpty(),
    featureGraphic = meta?.featureGraphic.orEmpty(),
    isAppCoins = true,
    bdsFlags = listOf("STORE_BDS"),
    versionName = packageInfo.versionName.orEmpty(),
    file = emptyApp.file.copy(size = apkSize),
    description = "Your favourite dice game. Roll, score and buy extra attempts in-game.",
  )
}
