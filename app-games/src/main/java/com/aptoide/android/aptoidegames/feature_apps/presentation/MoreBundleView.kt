package com.aptoide.android.aptoidegames.feature_apps.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
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
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.ads.native_ads.BrowseAdEligibility
import com.aptoide.android.aptoidegames.ads.native_ads.NativeAdListCard
import com.aptoide.android.aptoidegames.ads.native_ads.NativeAdListPlan
import com.aptoide.android.aptoidegames.ads.native_ads.NativeAdListSlotEffect
import com.aptoide.android.aptoidegames.ads.native_ads.NativeAdPlacement
import com.aptoide.android.aptoidegames.ads.native_ads.NativeAdUiState
import com.aptoide.android.aptoidegames.ads.native_ads.NativeAdViewModel
import com.aptoide.android.aptoidegames.ads.native_ads.itemsWithNativeAd
import com.aptoide.android.aptoidegames.ads.native_ads.rememberCollectionListNativeAd
import com.aptoide.android.aptoidegames.analytics.presentation.AnalyticsContext
import com.aptoide.android.aptoidegames.analytics.presentation.rememberGeneralAnalytics
import com.aptoide.android.aptoidegames.analytics.presentation.withAnalytics
import com.aptoide.android.aptoidegames.analytics.presentation.withItemPosition
import com.aptoide.android.aptoidegames.appview.buildAppViewRoute
import com.aptoide.android.aptoidegames.design_system.PrimarySmallButton
import com.aptoide.android.aptoidegames.error_views.GenericErrorView
import com.aptoide.android.aptoidegames.error_views.NoConnectionView
import com.aptoide.android.aptoidegames.feature_rtb.presentation.isRTB
import com.aptoide.android.aptoidegames.home.LoadingView
import com.aptoide.android.aptoidegames.installer.presentation.InstallViewShort
import com.aptoide.android.aptoidegames.mmp.UTMContext
import com.aptoide.android.aptoidegames.mmp.WithUTM
import com.aptoide.android.aptoidegames.mmp.getBundleSeeAllUTMInfo
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
    utmInfo = getBundleSeeAllUTMInfo(bundleTag),
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
        collectionNativeAd = rememberCollectionListNativeAd(),
      )
    }
  }
}

/**
 * @param collectionNativeAd opt-in native slot for ordinary See All lists. Only the plain
 *   See All screen passes it; RTB lists (which also render through this composable) and every
 *   other caller leave it null and are never given an ad. An RTB analytics context disables the
 *   slot even if a caller passes it.
 */
@Composable
fun AppsList(
  appList: List<App>,
  navigate: (String) -> Unit,
  handleRTBAdClick: (String, Int) -> Unit = { _, _ -> },
  onItemVisible: (Int) -> Unit = {},
  collectionNativeAd: NativeAdViewModel? = null,
) {
  val analyticsContext = AnalyticsContext.current
  val utmContext = UTMContext.current
  val bundleAnalytics = rememberBundleAnalytics()
  val lazyListState = rememberImpressionTrackingListState(onItemVisible)
  val isRTB = analyticsContext.isRTB()
  val nativeAd = collectionNativeAd?.takeIf { BrowseAdEligibility.allowed(it.placement, isRTB) }
  val nativeAdState = nativeAd?.uiState?.collectAsState()?.value ?: NativeAdUiState.Hidden
  if (nativeAd != null) {
    // The impression-tracking list state is only read here, its tracking is untouched.
    NativeAdListSlotEffect(viewModel = nativeAd, itemCount = appList.size, listState = lazyListState)
  }
  // Stable organic keys only for the opted-in ordinary list; RTB and every other caller keep the
  // default index keys exactly as before.
  val organicKeys = remember(appList, nativeAd != null) {
    if (nativeAd != null) NativeAdListPlan.uniqueKeys(appList.map { it.packageName }) else null
  }

  Spacer(modifier = Modifier.fillMaxWidth())
  LazyColumn(
    state = lazyListState,
    modifier = Modifier
      .semantics { collectionInfo = CollectionInfo(appList.size, 1) }
      .padding(start = 16.dp, end = 16.dp)
      .wrapContentSize(Alignment.TopCenter)
  ) {
    itemsWithNativeAd(
      items = appList,
      nativeAd = nativeAdState,
      placement = NativeAdPlacement.COLLECTION_LIST,
      key = organicKeys?.let { keys -> { index: Int, _: App -> keys[index] } },
      adContent = { loaded ->
        if (nativeAd != null) {
          NativeAdListCard(
            ad = loaded.ad,
            template = nativeAd.placement.template,
            onRender = nativeAd::render,
            modifier = Modifier.padding(vertical = 16.dp),
          )
        }
      },
    ) { index, app ->
      val onClick = {
        app.campaigns?.toAptoideMMPCampaign()?.sendClickEvent(utmContext)
        bundleAnalytics.sendAppPromoClick(
          app = app,
          analyticsContext = analyticsContext.copy(itemPosition = index)
        )
        if (isRTB) {
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
      }
      if (isRTB) {
        Box {
          AppItem(
            app = app,
            onClick = onClick,
          ) {
            PrimarySmallButton(
              onClick = {},
              title = stringResource(R.string.search_sponsored_install),
            )
          }
          Box(modifier = Modifier.matchParentSize().clickable(onClick = onClick))
        }
      } else {
        AppItem(
          app = app,
          onClick = onClick,
        ) {
          InstallViewShort(app, onNavigateToAppView = onClick)
        }
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
