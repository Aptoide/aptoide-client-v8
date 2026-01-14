package com.aptoide.android.aptoidegames.feature_apps.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.CollectionInfo
import androidx.compose.ui.semantics.collectionInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.extensions.PreviewDark
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.randomApp
import cm.aptoide.pt.feature_apps.presentation.AppsListUiState
import cm.aptoide.pt.feature_apps.presentation.previewAppsListIdleState
import cm.aptoide.pt.feature_apps.presentation.rememberAppsByTag
import cm.aptoide.pt.feature_campaigns.toAptoideMMPCampaign
import cm.aptoide.pt.feature_home.domain.Bundle
import cm.aptoide.pt.feature_home.domain.randomBundle
import com.aptoide.android.aptoidegames.analytics.presentation.AnalyticsContext
import com.aptoide.android.aptoidegames.analytics.presentation.SwipeListener
import com.aptoide.android.aptoidegames.analytics.presentation.withItemPosition
import com.aptoide.android.aptoidegames.appview.AppRatingAndDownloads
import com.aptoide.android.aptoidegames.appview.buildAppViewRoute
import com.aptoide.android.aptoidegames.drawables.icons.getBonusIconRight
import com.aptoide.android.aptoidegames.feature_rtb.presentation.isRTB
import com.aptoide.android.aptoidegames.home.BundleHeader
import com.aptoide.android.aptoidegames.home.LoadingBundleView
import com.aptoide.android.aptoidegames.home.getSeeMoreRouteNavigation
import com.aptoide.android.aptoidegames.installer.presentation.AppIconWProgress
import com.aptoide.android.aptoidegames.mmp.UTMContext
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import com.aptoide.android.aptoidegames.theme.Palette

@Composable
fun AppsGridBundle(
  bundle: Bundle,
  navigate: (String) -> Unit,
  spaceBy: Int = 0
) {
  val (uiState, _) = rememberAppsByTag(bundle.tag, bundle.timestamp)

  RealAppsGridBundle(
    bundle = bundle,
    uiState = uiState,
    navigate = navigate,
    spaceBy = spaceBy,
  )
}

@Composable
private fun RealAppsGridBundle(
  bundle: Bundle,
  uiState: AppsListUiState,
  navigate: (String) -> Unit,
  spaceBy: Int = 0,
) {
  Column {
    BundleHeader(
      title = bundle.title,
      icon = bundle.bundleIcon,
      hasMoreAction = bundle.hasMoreAction,
      onClick = getSeeMoreRouteNavigation(bundle = bundle, navigate = navigate)
    )
    when (uiState) {
      is AppsListUiState.Idle -> {
        AppsRowView(
          appsList = uiState.apps,
          navigate = navigate,
        )
        Spacer(Modifier.size(spaceBy.dp))
      }

      AppsListUiState.Empty,
      AppsListUiState.Error,
      AppsListUiState.NoConnection,
        -> Unit

      AppsListUiState.Loading -> {
        LoadingBundleView(height = 184.dp)
        Spacer(Modifier.size(spaceBy.dp))
      }
    }
  }
}

@Composable
internal fun AppsRowView(
  appsList: List<App>,
  navigate: (String) -> Unit,
  onRTBAdClick: (String, Int) -> Unit = { _, _ -> }
) {
  val analyticsContext = AnalyticsContext.current
  val utmContext = UTMContext.current
  val bundleAnalytics = rememberBundleAnalytics()
  val lazyListState = rememberLazyListState()

  SwipeListener(interactionSource = lazyListState.interactionSource)
  LazyRow(
    modifier = Modifier
      .semantics {
        collectionInfo = CollectionInfo(1, appsList.size)
      }
      .fillMaxWidth()
      .wrapContentHeight(),
    state = lazyListState,
    contentPadding = PaddingValues(horizontal = 16.dp),
    horizontalArrangement = Arrangement.spacedBy(24.dp),
  ) {
    itemsIndexed(appsList) { index, item ->
      AppGridView(
        app = item,
        onClick = {
          item.campaigns?.toAptoideMMPCampaign()?.sendClickEvent(utmContext)
          bundleAnalytics.sendAppPromoClick(
            app = item,
            analyticsContext = analyticsContext.copy(itemPosition = index)
          )
          if (analyticsContext.isRTB()) {
            onRTBAdClick(item.packageName, index)
          } else {
            navigate(
              buildAppViewRoute(
                appSource = item,
                utmCampaign = item.campaigns?.campaignId
              )
                .withItemPosition(index)
            )
          }
        },
      )
    }
  }
}

@Composable
fun AppGridView(
  app: App,
  onClick: () -> Unit,
) {
  Column(
    modifier = Modifier
      .semantics(mergeDescendants = true) { }
      .clickable(onClick = onClick)
      .width(88.dp)
      .wrapContentSize(Alignment.TopCenter)
  ) {
    Box(
      contentAlignment = Alignment.TopEnd
    ) {
      AppIconWProgress(
        app = app,
        contentDescription = null,
        modifier = Modifier.size(88.dp),
      )
      if (app.isAppCoins) {
        Image(
          imageVector = getBonusIconRight(
            iconColor = Palette.Primary,
            outlineColor = Palette.Black,
            backgroundColor = Palette.Secondary
          ),
          contentDescription = null,
          modifier = Modifier.size(32.dp),
        )
      }
    }
    Text(
      text = app.name,
      color = Palette.White,
      maxLines = 2,
      overflow = TextOverflow.Ellipsis,
      modifier = Modifier
        .padding(top = 8.dp)
        .defaultMinSize(minHeight = 36.dp),
      style = AGTypography.DescriptionGames
    )
    AppRatingAndDownloads(rating = app.pRating)
  }
}

@PreviewDark
@Composable
private fun RealAppsGridBundlePreview() {
  AptoideTheme {
    RealAppsGridBundle(
      bundle = randomBundle,
      uiState = AppsListUiState.Idle(
        apps = listOf(
          randomApp,
          randomApp,
          randomApp,
        )
      ),
      navigate = {},
    )
  }
}

@PreviewDark
@Composable
private fun AppsRowViewPreview() {
  AptoideTheme {
    AppsRowView(
      appsList = previewAppsListIdleState.apps,
      navigate = {},
    )
  }
}

@PreviewDark
@Composable
private fun AppGridViewPreview() {
  AptoideTheme {
    AppGridView(
      app = randomApp,
      onClick = {},
    )
  }
}
