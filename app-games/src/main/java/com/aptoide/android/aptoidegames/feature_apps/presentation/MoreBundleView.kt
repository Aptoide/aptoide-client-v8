package com.aptoide.android.aptoidegames.feature_apps.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.CollectionInfo
import androidx.compose.ui.semantics.collectionInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.navigation.navDeepLink
import cm.aptoide.pt.extensions.PreviewDark
import cm.aptoide.pt.extensions.ScreenData
import cm.aptoide.pt.extensions.getRandomString
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.presentation.AppsListUiState
import cm.aptoide.pt.feature_apps.presentation.AppsListUiStateProvider
import cm.aptoide.pt.feature_apps.presentation.rememberAppsByTag
import cm.aptoide.pt.feature_campaigns.toAptoideMMPCampaign
import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.analytics.presentation.AnalyticsContext
import com.aptoide.android.aptoidegames.analytics.presentation.rememberGeneralAnalytics
import com.aptoide.android.aptoidegames.analytics.presentation.withAnalytics
import com.aptoide.android.aptoidegames.analytics.presentation.withItemPosition
import com.aptoide.android.aptoidegames.appview.buildAppViewRoute
import com.aptoide.android.aptoidegames.error_views.GenericErrorView
import com.aptoide.android.aptoidegames.error_views.NoConnectionView
import com.aptoide.android.aptoidegames.feature_rtb.presentation.isRTB
import com.aptoide.android.aptoidegames.home.LoadingView
import com.aptoide.android.aptoidegames.installer.presentation.InstallViewShort
import com.aptoide.android.aptoidegames.mmp.UTMContext
import com.aptoide.android.aptoidegames.mmp.WithUTM
import com.aptoide.android.aptoidegames.mmp.getUTMConfig
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import com.aptoide.android.aptoidegames.toolbar.AppGamesTopBar

const val seeMoreRoute = "seeMore/{title}/{tag}"

fun seeMoreScreen() = ScreenData.withAnalytics(
  route = seeMoreRoute,
  screenAnalyticsName = "SeeAll",
  deepLinks = listOf(navDeepLink { uriPattern = BuildConfig.DEEP_LINK_SCHEMA + seeMoreRoute })
) { arguments, navigate, navigateBack ->
  val bundleTitle = arguments?.getString("title")!!
  val bundleTag = arguments.getString("tag")!!

  WithUTM(
    content = getUTMConfig(bundleTag)?.seeAllContent,
    navigate = navigate
  ) { navigate ->
    MoreBundleView(
      title = bundleTitle,
      bundleTag = bundleTag,
      navigateBack = navigateBack,
      navigate = navigate,
    )
  }
}

fun buildSeeMoreRoute(
  title: String,
  bundleTag: String,
) = "seeMore/$title/$bundleTag"

fun buildSeeMoreDeepLinkUri(
  title: String,
  bundleTag: String,
) = BuildConfig.DEEP_LINK_SCHEMA + buildSeeMoreRoute(title, bundleTag)

@Composable
fun MoreBundleView(
  title: String,
  bundleTag: String,
  navigateBack: () -> Unit,
  navigate: (String) -> Unit,
) {
  val (uiState, reload) = rememberAppsByTag(bundleTag)
  val analyticsContext = AnalyticsContext.current
  val generalAnalytics = rememberGeneralAnalytics()

  MoreBundleViewContent(
    uiState = uiState,
    title = title,
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
fun MoreBundleViewContent(
  uiState: AppsListUiState,
  title: String,
  reload: () -> Unit,
  noNetworkReload: () -> Unit,
  navigateBack: () -> Unit,
  navigate: (String) -> Unit,
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .fillMaxHeight(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    AppGamesTopBar(navigateBack = navigateBack, title = title)
    when (uiState) {
      AppsListUiState.Loading -> LoadingView()
      AppsListUiState.NoConnection -> NoConnectionView(onRetryClick = noNetworkReload)
      AppsListUiState.Error -> GenericErrorView(reload)
      AppsListUiState.Empty -> AppsList(
        appList = emptyList(),
        navigate = navigate,
      )

      is AppsListUiState.Idle -> AppsList(
        appList = uiState.apps,
        navigate = navigate,
      )
    }
  }
}

@Composable
fun AppsList(
  appList: List<App>,
  navigate: (String) -> Unit,
  handleRTBAdClick: (String, Int) -> Unit = { _, _ -> },
) {
  val analyticsContext = AnalyticsContext.current
  val utmContext = UTMContext.current
  val bundleAnalytics = rememberBundleAnalytics()

  Spacer(modifier = Modifier.fillMaxWidth())
  LazyColumn(
    modifier = Modifier
      .semantics { collectionInfo = CollectionInfo(appList.size, 1) }
      .padding(start = 16.dp, end = 16.dp)
      .wrapContentSize(Alignment.TopCenter)
  ) {
    itemsIndexed(appList) { index, app ->
      AppItem(
        app = app,
        onClick = {
          app.campaigns?.toAptoideMMPCampaign()?.sendClickEvent(utmContext)
          bundleAnalytics.sendAppPromoClick(
            app = app,
            analyticsContext = analyticsContext.copy(itemPosition = index)
          )
          if (analyticsContext.isRTB()) {
            handleRTBAdClick(app.packageName, index)
          } else {
            navigate(
              buildAppViewRoute(
                appSource = app,
                utmCampaign = app.campaigns?.campaignId,
              )
                .withItemPosition(index)
            )
          }
        },
      ) {
        InstallViewShort(app)
      }
    }
  }
}

@PreviewDark
@Composable
private fun MoreBundleViewPreview(
  @PreviewParameter(AppsListUiStateProvider::class) uiState: AppsListUiState,
) {
  AptoideTheme {
    MoreBundleViewContent(
      uiState = uiState,
      title = getRandomString(range = 1..5, capitalize = true),
      reload = {},
      noNetworkReload = {},
      navigateBack = {},
      navigate = {},
    )
  }
}
