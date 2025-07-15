package com.aptoide.android.aptoidegames.feature_rtb.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.navDeepLink
import cm.aptoide.pt.extensions.ScreenData
import cm.aptoide.pt.feature_apps.presentation.AppsListUiState
import cm.aptoide.pt.feature_campaigns.toAptoideMMPCampaign
import cm.aptoide.pt.feature_home.domain.Bundle
import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.analytics.presentation.AnalyticsContext
import com.aptoide.android.aptoidegames.analytics.presentation.rememberGeneralAnalytics
import com.aptoide.android.aptoidegames.analytics.presentation.withAnalytics
import com.aptoide.android.aptoidegames.analytics.presentation.withBundleMeta
import com.aptoide.android.aptoidegames.feature_apps.presentation.MoreBonusBundleScreen
import com.aptoide.android.aptoidegames.home.analytics.meta

const val rtbSeeMoreRoute = "rtbSeeMore/{tag}"
private var hasSentImpression = false

fun rtbSeeMoreScreen() = ScreenData.withAnalytics(
  route = rtbSeeMoreRoute,
  screenAnalyticsName = "SeeAll",
  deepLinks = listOf(navDeepLink { uriPattern = BuildConfig.DEEP_LINK_SCHEMA + rtbSeeMoreRoute })
) { arguments, navigate, navigateBack ->
  val bundleTag = arguments?.getString("tag")!!

  RtbMoreBundleScreen(
    bundleTag = bundleTag,
    navigateBack = navigateBack,
    navigate = navigate,
  )
}

fun buildRtbSeeMoreRoute(
  bundleTag: String,
) = "rtbSeeMore/$bundleTag"

@Composable
private fun RTBMoreAptoideMMPController(
  uiState: AppsListUiState,
  bundleTag: String,
  placement: String,
) {
  when (uiState) {
    is AppsListUiState.Idle ->
      uiState.apps.forEachIndexed { index, rtbApp ->
        if (!hasSentImpression) {
          rtbApp.campaigns?.toAptoideMMPCampaign()
            ?.sendImpressionEvent(bundleTag, rtbApp.packageName)
          rtbApp.campaigns?.run {
            placementType = placement
          }
          if (index == uiState.apps.size - 1) {
            hasSentImpression = true
          }
        }
      }

    else -> {}
  }
}

@Composable
private fun RtbMoreBundleScreen(
  bundleTag: String,
  navigateBack: () -> Unit,
  navigate: (String) -> Unit,
) {
  val (uiState, reload) = rememberRTBApps(bundleTag)
  val analyticsContext = AnalyticsContext.current
  val generalAnalytics = rememberGeneralAnalytics()

  RTBMoreAptoideMMPController(uiState, bundleTag, "home-bundle")

  MoreBonusBundleScreen(
    uiState = uiState,
    title = stringResource(R.string.bonus_banner_title, "20"),
    bundleTag = bundleTag,
    reload = reload,
    noNetworkReload = {
      reload()
    },
    navigateBack = {
      generalAnalytics.sendBackButtonClick(analyticsContext.copy(itemPosition = null))
      navigateBack()
    },
    navigate = navigate,
  )
}

@Composable
fun getRTBMoreRouteNavigation(
  bundle: Bundle,
  navigate: (String) -> Unit,
): () -> Unit = {
  navigate(
    buildRtbSeeMoreRoute("${bundle.tag}-more")
      .withBundleMeta(bundle.meta.copy(tag = "${bundle.tag}-more"))
  )
}
