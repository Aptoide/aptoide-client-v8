package com.aptoide.android.aptoidegames.feature_rtb.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.navDeepLink
import cm.aptoide.pt.extensions.ScreenData
import cm.aptoide.pt.feature_campaigns.toAptoideMMPCampaign
import cm.aptoide.pt.feature_home.domain.Bundle
import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.analytics.presentation.AnalyticsContext
import com.aptoide.android.aptoidegames.analytics.presentation.rememberGeneralAnalytics
import com.aptoide.android.aptoidegames.analytics.presentation.withAnalytics
import com.aptoide.android.aptoidegames.analytics.presentation.withBundleMeta
import com.aptoide.android.aptoidegames.error_views.GenericErrorView
import com.aptoide.android.aptoidegames.error_views.NoConnectionView
import com.aptoide.android.aptoidegames.feature_apps.presentation.AppsList
import com.aptoide.android.aptoidegames.feature_apps.presentation.rememberBundleAnalytics
import com.aptoide.android.aptoidegames.feature_rtb.data.RTBApp
import com.aptoide.android.aptoidegames.feature_rtb.data.RTBAppsListUiState
import com.aptoide.android.aptoidegames.home.LoadingView
import com.aptoide.android.aptoidegames.home.analytics.meta
import com.aptoide.android.aptoidegames.home.translateOrKeep
import com.aptoide.android.aptoidegames.mmp.UTMContext
import com.aptoide.android.aptoidegames.mmp.WithUTM
import com.aptoide.android.aptoidegames.toolbar.AppGamesTopBar

const val rtbSeeMoreRoute = "rtbSeeMore/{tag}"
private var hasSentImpression = false

fun rtbSeeMoreScreen() = ScreenData.withAnalytics(
  route = rtbSeeMoreRoute,
  screenAnalyticsName = "SeeAll",
  deepLinks = listOf(navDeepLink { uriPattern = BuildConfig.DEEP_LINK_SCHEMA + rtbSeeMoreRoute })
) { arguments, navigate, navigateBack ->
  val bundleTag = arguments?.getString("tag")!!

  WithUTM(
    medium = "rtb",
    campaign = "regular",
    content = "rtb-seeall",
    navigate = navigate,
    shouldSendClickEvents = true
  ) { navigate ->
    RTBMoreBundleScreen(
      navigateBack = navigateBack,
      navigate = navigate,
    )
  }
}

fun buildRtbSeeMoreRoute(
  bundleTag: String,
) = "rtbSeeMore/$bundleTag"

@Composable
private fun RTBMoreAptoideMMPController(
  uiState: RTBAppsListUiState,
) {
  when (uiState) {
    is RTBAppsListUiState.Idle ->
      uiState.apps.forEachIndexed { index, rtbApp ->
        val app = rtbApp.app
        if (!hasSentImpression) {
          app.campaigns?.toAptoideMMPCampaign()
            ?.sendImpressionEvent(UTMContext.current, app.packageName)
          if (index == uiState.apps.size - 1) {
            hasSentImpression = true
          }
        }
      }

    else -> {}
  }
}

@Composable
fun RTBMoreBundleScreen(
  navigateBack: () -> Unit,
  navigate: (String) -> Unit,
) {
  val (uiState, reload) = rememberRTBApps("home-bundle")
  val analyticsContext = AnalyticsContext.current
  val generalAnalytics = rememberGeneralAnalytics()
  var isLoading by remember { mutableStateOf(false) }

  RTBMoreAptoideMMPController(uiState)

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .fillMaxHeight(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    AppGamesTopBar(
      navigateBack = {
        generalAnalytics.sendBackButtonClick(analyticsContext.copy(itemPosition = null))
        navigateBack()
      },
      title = "Highlighted".translateOrKeep(LocalContext.current)
    )
    when {
      uiState is RTBAppsListUiState.Loading || isLoading -> LoadingView()
      uiState is RTBAppsListUiState.NoConnection -> NoConnectionView(reload)
      uiState is RTBAppsListUiState.Error -> GenericErrorView(reload)
      uiState is RTBAppsListUiState.Empty -> RTBMoreAppsList(
        rtbAppsList = emptyList(),
        navigate = navigate,
        onLoadingChange = { isLoading = it }
      )

      uiState is RTBAppsListUiState.Idle -> RTBMoreAppsList(
        rtbAppsList = uiState.apps,
        navigate = navigate,
        onLoadingChange = { isLoading = it }
      )
    }
  }
}

@Composable
private fun RTBMoreAppsList(
  navigate: (String) -> Unit,
  rtbAppsList: List<RTBApp>,
  onLoadingChange: (Boolean) -> Unit
) {
  val handleRTBAdClick = rememberRTBAdClickHandler(
    rtbAppsList = rtbAppsList,
    navigate = navigate,
    onLoadingChange = onLoadingChange
  )

  val appsList = rtbAppsList.map { it.app }
  AppsList(appList = appsList, navigate = navigate, handleRTBAdClick)
}

@Composable
fun getRTBMoreRouteNavigation(
  bundle: Bundle,
  navigate: (String) -> Unit,
): () -> Unit {
  val analyticsContext = AnalyticsContext.current
  val bundleAnalytics = rememberBundleAnalytics()
  return {
    bundleAnalytics.sendSeeAllClick(analyticsContext)
    navigate(
      buildRtbSeeMoreRoute("${bundle.tag}-more")
        .withBundleMeta(bundle.meta.copy(tag = "${bundle.tag}-more"))
    )
  }
}
