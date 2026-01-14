package com.aptoide.android.aptoidegames.mmp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import cm.aptoide.pt.feature_campaigns.AptoideMMPCampaign
import cm.aptoide.pt.feature_campaigns.UTMInfo
import cm.aptoide.pt.feature_home.domain.Bundle

/**
 * Sets up UTM tracking parameters for a given bundle based on its tag.
 * This should be called once per bundle to register its UTM information.
 *
 * @param bundle The bundle to configure UTM tracking for
 */
@Composable
fun BundleUTMSetup(bundle: Bundle) {
  LaunchedEffect(Unit) {
    val utmConfig = getUTMConfig(bundle.tag) ?: return@LaunchedEffect

    AptoideMMPCampaign.allowedBundleTags[bundle.tag] = UTMInfo(
      utmMedium = "store-placement",
      utmCampaign = "organic-discovery",
      utmContent = utmConfig.homeContent
    )

    if (bundle.hasMoreAction) {
      AptoideMMPCampaign.allowedBundleTags["${bundle.tag}-more"] = UTMInfo(
        utmMedium = "store-placement",
        utmCampaign = "organic-discovery",
        utmContent = utmConfig.seeAllContent
      )
    }
  }
}

data class UTMConfig(
  val homeContent: String,
  val seeAllContent: String
)

private const val MORE_SUFFIX = "-more"

fun getUTMConfig(bundleTag: String): UTMConfig? {
  val baseTag = bundleTag.removeSuffix(MORE_SUFFIX)
  return when (baseTag) {
    "apps-group-just-arrived" -> UTMConfig("home-just-arrived", "just-arrived-seeall")
    "apps-group-trending" -> UTMConfig("home-trending", "trending-seeall")
    "apps-group-editors-choice" -> UTMConfig("home-editors-choice", "editors-choice-seeall")
    "apps-group-featured-appcoins" -> UTMConfig("home-get-rewarded", "get-rewarded-seeall")
    else -> null
  }
}
