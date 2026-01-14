package com.aptoide.android.aptoidegames.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.CollectionInfo
import androidx.compose.ui.semantics.collectionInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.extensions.PreviewDark
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.randomApp
import cm.aptoide.pt.feature_apps.presentation.AppsListUiState
import cm.aptoide.pt.feature_apps.presentation.AppsListUiStateProvider
import cm.aptoide.pt.feature_apps.presentation.appsBySortType
import cm.aptoide.pt.feature_campaigns.toAptoideMMPCampaign
import com.aptoide.android.aptoidegames.analytics.presentation.AnalyticsContext
import com.aptoide.android.aptoidegames.analytics.presentation.withItemPosition
import com.aptoide.android.aptoidegames.appview.buildAppViewRoute
import com.aptoide.android.aptoidegames.drawables.icons.getBonusIconRight
import com.aptoide.android.aptoidegames.error_views.GenericErrorView
import com.aptoide.android.aptoidegames.error_views.NoConnectionView
import com.aptoide.android.aptoidegames.feature_apps.presentation.rememberBundleAnalytics
import com.aptoide.android.aptoidegames.installer.presentation.AppIconWProgress
import com.aptoide.android.aptoidegames.installer.presentation.InstallViewShort
import com.aptoide.android.aptoidegames.installer.presentation.ProgressTextWithDownloads
import com.aptoide.android.aptoidegames.mmp.UTMContext
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import com.aptoide.android.aptoidegames.theme.Palette
import kotlin.random.Random
import kotlin.random.nextInt

@Composable
fun TopChartsView(
  sort: String?,
  navigate: (String) -> Unit
) {
  val (uiState, reload) = appsBySortType(sort = sort ?: "pdownloads")

  TopChartsViewContent(
    uiState = uiState,
    navigate = navigate,
    reload = reload
  )
}

@Composable
fun TopChartsViewContent(
  uiState: AppsListUiState,
  navigate: (String) -> Unit,
  reload: () -> Unit
) {
  when (uiState) {
    AppsListUiState.Loading -> LoadingView()
    AppsListUiState.NoConnection -> NoConnectionView(onRetryClick = reload)
    AppsListUiState.Error -> GenericErrorView(reload)
    AppsListUiState.Empty -> TopChartsList(
      apps = emptyList(),
      navigate = navigate,
    )

    is AppsListUiState.Idle -> TopChartsList(
      apps = uiState.apps,
      navigate = navigate
    )
  }
}

@Composable
fun TopChartsList(
  apps: List<App>,
  navigate: (String) -> Unit
) {
  val analyticsContext = AnalyticsContext.current
  val utmContext = UTMContext.current
  val bundleAnalytics = rememberBundleAnalytics()

  LazyColumn(
    modifier = Modifier
      .semantics { collectionInfo = CollectionInfo(apps.size, 1) }
      .wrapContentSize(Alignment.TopCenter),
    verticalArrangement = Arrangement.spacedBy(32.dp),
    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 24.dp)
  ) {
    itemsIndexed(apps) { index, app ->
      ChartsAppItem(
        app = app,
        rank = index + 1,
        onClick = {
          app.campaigns?.toAptoideMMPCampaign()?.sendClickEvent(utmContext)
          bundleAnalytics.sendAppPromoClick(
            app = app,
            analyticsContext = analyticsContext.copy(itemPosition = index)
          )
          navigate(
            buildAppViewRoute(app).withItemPosition(index)
          )
        },
      ) {
        InstallViewShort(app)
      }
    }
  }
}

@Composable
fun ChartsAppItem(
  modifier: Modifier = Modifier,
  app: App,
  rank: Int,
  onClick: () -> Unit,
  installButton: @Composable RowScope.() -> Unit,
) {
  Row(
    modifier = modifier
      .clickable(onClick = onClick)
      .fillMaxWidth()
      .defaultMinSize(minHeight = 64.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Box(
      contentAlignment = Alignment.TopEnd
    ) {
      AppIconWProgress(
        app = app,
        contentDescription = null,
        modifier = Modifier.size(64.dp),
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
    Column(
      modifier = Modifier
        .padding(start = 16.dp, end = 16.dp)
        .weight(1f),
      verticalArrangement = Arrangement.SpaceEvenly
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Text(
          text = rank.toString(),
          style = AGTypography.InputsM,
          maxLines = 1,
          color = Palette.Primary
        )
        Text(
          text = app.name,
          style = AGTypography.DescriptionGames,
          overflow = TextOverflow.Ellipsis,
          maxLines = 1,
          color = Palette.White
        )
      }
      ProgressTextWithDownloads(app = app)
    }
    installButton()
  }
}

@PreviewDark
@Composable
fun ChartsAppItemPreview() {
  AptoideTheme {
    randomApp.let {
      ChartsAppItem(
        app = it,
        rank = Random.nextInt(1..50),
        onClick = {}
      ) {
        InstallViewShort(app = it)
      }
    }
  }
}

@PreviewDark
@Composable
private fun TopChartsViewPreview(
  @PreviewParameter(AppsListUiStateProvider::class) uiState: AppsListUiState,
) {
  AptoideTheme {
    TopChartsViewContent(
      uiState = uiState,
      navigate = {},
      reload = {}
    )
  }
}
