package com.aptoide.android.aptoidegames.categories.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CollectionInfo
import androidx.compose.ui.semantics.collectionInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.extensions.PreviewDark
import cm.aptoide.pt.extensions.ScreenData
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.presentation.AppsListUiState
import cm.aptoide.pt.feature_apps.presentation.AppsListUiStateProvider
import cm.aptoide.pt.feature_apps.presentation.categoryApps
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.ads.native_ads.NativeAdListCard
import com.aptoide.android.aptoidegames.ads.native_ads.NativeAdListSlotEffect
import com.aptoide.android.aptoidegames.ads.native_ads.itemsWithNativeAd
import com.aptoide.android.aptoidegames.ads.native_ads.rememberCategoryListNativeAd
import com.aptoide.android.aptoidegames.analytics.presentation.AnalyticsContext
import com.aptoide.android.aptoidegames.analytics.presentation.rememberGeneralAnalytics
import com.aptoide.android.aptoidegames.analytics.presentation.withAnalytics
import com.aptoide.android.aptoidegames.analytics.presentation.withBundleMeta
import com.aptoide.android.aptoidegames.analytics.presentation.withItemPosition
import com.aptoide.android.aptoidegames.appview.buildAppViewRoute
import com.aptoide.android.aptoidegames.error_views.EmptyView
import com.aptoide.android.aptoidegames.error_views.GenericErrorView
import com.aptoide.android.aptoidegames.error_views.NoConnectionView
import com.aptoide.android.aptoidegames.feature_apps.presentation.AppItem
import com.aptoide.android.aptoidegames.feature_apps.presentation.LargeAppItem
import com.aptoide.android.aptoidegames.feature_apps.presentation.rememberBundleAnalytics
import com.aptoide.android.aptoidegames.home.LoadingView
import com.aptoide.android.aptoidegames.installer.presentation.InstallViewShort
import com.aptoide.android.aptoidegames.installer.presentation.reportingCampaignClick
import com.aptoide.android.aptoidegames.toolbar.AppGamesTopBar

const val categoryDetailRoute = "category/{title}/{name}"

fun categoryDetailScreen() = ScreenData.withAnalytics(
  route = categoryDetailRoute,
  screenAnalyticsName = "CategoryView"
) { arguments, navigate, navigateBack ->
  val categoryTitle = arguments?.getString("title")!!
  val categoryName = arguments.getString("name")!!
  val (uiState, reload) = categoryApps(categoryName)

  CategoryDetailView(
    title = categoryTitle,
    categoryName = categoryName,
    uiState = uiState,
    onError = reload,
    navigateBack = navigateBack,
    navigate = navigate,
  )
}

fun buildCategoryDetailRoute(
  title: String,
  name: String,
) = "category/$title/$name"

@Composable
fun CategoryDetailView(
  title: String,
  categoryName: String,
  uiState: AppsListUiState,
  onError: () -> Unit,
  navigateBack: () -> Unit,
  navigate: (String) -> Unit,
) {
  val analyticsContext = AnalyticsContext.current
  val generalAnalytics = rememberGeneralAnalytics()
  val bundleAnalytics = rememberBundleAnalytics()

  val navigateToApp = { app: App, index: Int ->
    navigate(
      buildAppViewRoute(app)
        .withItemPosition(index)
        .withBundleMeta(analyticsContext.bundleMeta?.copy(tag = categoryName))
    )
  }

  CategoryDetailViewContent {
    AppGamesTopBar(
      navigateBack = {
        generalAnalytics.sendBackButtonClick(
          analyticsContext = analyticsContext.copy(itemPosition = null)
        )
        navigateBack()
      },
      title = title
    )
    val categoryNativeAd = rememberCategoryListNativeAd()
    val categoryNativeAdState by categoryNativeAd.uiState.collectAsState()
    val categoryListState = rememberLazyListState()
    NativeAdListSlotEffect(
      viewModel = categoryNativeAd,
      itemCount = (uiState as? AppsListUiState.Idle)?.apps?.size ?: 0,
      listState = categoryListState,
    )
    when (uiState) {
      is AppsListUiState.Loading -> LoadingView()
      is AppsListUiState.Empty -> EmptyView(text = stringResource(R.string.empty_category_body))
      is AppsListUiState.NoConnection -> NoConnectionView(onRetryClick = onError)
      is AppsListUiState.Error -> GenericErrorView(onError)
      is AppsListUiState.Idle -> CategoryAppsList(
        size = uiState.apps.size,
        state = categoryListState,
      ) {
        // One native card after the first organic rows (browse_native_category_position); the
        // apps keep their original index on both sides of it.
        itemsWithNativeAd(
          items = uiState.apps,
          nativeAd = categoryNativeAdState,
          placement = categoryNativeAd.placement,
          key = { _, app -> app.packageName },
          adContent = { loaded ->
            NativeAdListCard(
              ad = loaded.ad,
              template = categoryNativeAd.placement.template,
              onRender = categoryNativeAd::render,
              modifier = Modifier.padding(vertical = 16.dp),
            )
          },
        ) { index, app ->

          val openAppView = {
            bundleAnalytics.sendAppPromoClick(
              app = app,
              analyticsContext = analyticsContext.copy(itemPosition = index)
            )
            navigateToApp(app, index)
          }
          val installViewShort: @Composable () -> Unit = {
            InstallViewShort(
              app = app,
              onInstallStarted = {},
              onNavigateToAppView = openAppView.reportingCampaignClick(app),
            )
          }
          if (index == 0) {
            LargeAppItem(
              app = app,
              onClick = openAppView
            ) {
              installViewShort()
            }
          } else {
            AppItem(
              app = app,
              onClick = openAppView,
            ) {
              installViewShort()
            }
          }
        }
      }
    }
  }
}

@Composable
fun CategoryDetailViewContent(content: @Composable ColumnScope.() -> Unit) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .fillMaxHeight(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    content()
  }
}

@Composable
fun CategoryAppsList(
  size: Int,
  state: LazyListState = rememberLazyListState(),
  content: LazyListScope.() -> Unit,
) {
  LazyColumn(
    state = state,
    modifier = Modifier
      .semantics { collectionInfo = CollectionInfo(size, 1) }
      .padding(start = 16.dp, end = 16.dp)
      .wrapContentSize(Alignment.TopCenter)
  ) {
    content()
  }
}

@PreviewDark
@Composable
fun CategoryDetailViewPreview(
  @PreviewParameter(AppsListUiStateProvider::class) uiState: AppsListUiState,
) {
  CategoryDetailView(
    title = "Action",
    categoryName = "Action",
    uiState = uiState,
    onError = {},
    navigateBack = {},
    navigate = {},
  )
}
