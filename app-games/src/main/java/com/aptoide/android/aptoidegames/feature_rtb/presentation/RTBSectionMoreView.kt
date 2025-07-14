package com.aptoide.android.aptoidegames.feature_rtb.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.navDeepLink
import cm.aptoide.pt.extensions.ScreenData
import cm.aptoide.pt.feature_apps.presentation.AppsListUiState
import cm.aptoide.pt.feature_campaigns.toAptoideMMPCampaign
import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.analytics.presentation.AnalyticsContext
import com.aptoide.android.aptoidegames.analytics.presentation.rememberGeneralAnalytics
import com.aptoide.android.aptoidegames.analytics.presentation.withAnalytics
import com.aptoide.android.aptoidegames.feature_apps.presentation.MoreBonusBundleScreen

const val rtbSeeMoreRoute = "rtbSeeMore/{title}/{tag}"
private var hasSentImpression = false

fun rtbSeeMoreScreen() = ScreenData.withAnalytics(
  route = rtbSeeMoreRoute,
  screenAnalyticsName = "SeeAll",
  deepLinks = listOf(navDeepLink { uriPattern = BuildConfig.DEEP_LINK_SCHEMA + rtbSeeMoreRoute })
) { arguments, navigate, navigateBack ->
  val bundleTitle = arguments?.getString("title")!!
  val bundleTag = arguments.getString("tag")!!

  RtbMoreBundleScreen(
    title = bundleTitle,
    bundleTag = bundleTag,
    navigateBack = navigateBack,
    navigate = navigate,
  )
}

fun buildRtbSeeMoreRoute(
  title: String,
  bundleTag: String,
) = "rtbSeeMore/$title/$bundleTag"

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
  title: String,
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
    title = title,
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
