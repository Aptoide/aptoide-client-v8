package com.aptoide.android.aptoidegames.feature_apps.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
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
import cm.aptoide.pt.feature_apps.presentation.AppsListUiState.Empty
import cm.aptoide.pt.feature_apps.presentation.AppsListUiState.Error
import cm.aptoide.pt.feature_apps.presentation.AppsListUiState.Idle
import cm.aptoide.pt.feature_apps.presentation.AppsListUiState.Loading
import cm.aptoide.pt.feature_apps.presentation.AppsListUiState.NoConnection
import cm.aptoide.pt.feature_apps.presentation.rememberAppsByTag
import cm.aptoide.pt.feature_home.domain.Bundle
import cm.aptoide.pt.feature_home.domain.randomBundle
import com.aptoide.android.aptoidegames.AptoideAsyncImage
import com.aptoide.android.aptoidegames.AptoideFeatureGraphicImage
import com.aptoide.android.aptoidegames.appview.buildAppViewRoute
import com.aptoide.android.aptoidegames.home.BundleHeader
import com.aptoide.android.aptoidegames.home.EmptyBundleView
import com.aptoide.android.aptoidegames.home.LoadingBundleView
import com.aptoide.android.aptoidegames.home.getSeeMoreRouteNavigation
import com.aptoide.android.aptoidegames.installer.presentation.AppIconWProgress
import com.aptoide.android.aptoidegames.installer.presentation.InstallViewShort
import com.aptoide.android.aptoidegames.installer.presentation.ProgressText
import com.aptoide.android.aptoidegames.theme.AppTheme
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import com.aptoide.android.aptoidegames.theme.agWhite

@Composable
fun CarouselLargeBundle(
  bundle: Bundle,
  navigate: (String) -> Unit,
) {
  val (uiState, _) = rememberAppsByTag(bundle.tag, bundle.timestamp)

  RealCarouselLargeBundle(
    bundle = bundle,
    uiState = uiState,
    navigate = navigate
  )
}

@Composable
private fun RealCarouselLargeBundle(
  bundle: Bundle,
  uiState: AppsListUiState,
  navigate: (String) -> Unit
) {
  Box(
    modifier = Modifier.fillMaxWidth()
  ) {
    if (!bundle.background.isNullOrEmpty()) {
      AptoideAsyncImage(
        modifier = Modifier.matchParentSize(),
        data = bundle.background,
        contentDescription = "background image",
      )
    }
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 24.dp)
    ) {
      BundleHeader(
        title = bundle.title,
        icon = bundle.bundleIcon,
        hasMoreAction = bundle.hasMoreAction,
        onClick = getSeeMoreRouteNavigation(bundle = bundle, navigate = navigate),
        titleColor = agWhite,
      )
      when (uiState) {
        is Idle -> CarouselLargeListView(
          appsList = uiState.apps,
          navigate = navigate,
        )

        Empty,
        Error,
        NoConnection,
        -> EmptyBundleView(height = 184.dp)

        Loading -> LoadingBundleView(height = 184.dp)
      }
    }
  }
}

@Composable
private fun CarouselLargeListView(
  appsList: List<App>,
  navigate: (String) -> Unit,
) {
  val lazyListState = rememberLazyListState()

  LazyRow(
    modifier = Modifier
      .semantics {
        collectionInfo = CollectionInfo(1, appsList.size)
      }
      .fillMaxWidth()
      .wrapContentHeight(),
    state = lazyListState,
    horizontalArrangement = Arrangement.spacedBy(16.dp),
    contentPadding = PaddingValues(horizontal = 16.dp)
  ) {
    itemsIndexed(appsList) { index, item ->
      CarouselLargeAppView(
        app = item,
        onClick = {
          navigate(
            buildAppViewRoute(item.packageName)
          )
        },
      )
    }
  }
}

@Composable
private fun CarouselLargeAppView(
  app: App,
  onClick: () -> Unit,
) {
  Column(
    modifier = Modifier
      .semantics(mergeDescendants = true) { }
      .clickable(onClick = onClick)
      .requiredWidth(280.dp)
      .height(184.dp)
  ) {
    AptoideFeatureGraphicImage(
      modifier = Modifier
        .padding(bottom = 8.dp)
        .width(280.dp)
        .height(136.dp),
      data = app.featureGraphic,
      contentDescription = null,
    )
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      AppIconWProgress(
        app = app,
        contentDescription = null,
        modifier = Modifier.size(40.dp),
      )
      Column(
        modifier = Modifier
          .padding(start = 8.dp, end = 8.dp)
          .weight(1f),
      ) {
        Text(
          text = app.name,
          modifier = Modifier
            .wrapContentHeight()
            .weight(1f),
          color = agWhite,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis,
          style = AppTheme.typography.descriptionGames
        )
        ProgressText(
          app = app,
          showVersionName = false
        )
      }
      InstallViewShort(
        app = app,
        cancelable = false
      )
    }
  }
}

@PreviewDark
@Composable
fun RealCarouselLargeBundlePreview() {
  AptoideTheme {
    RealCarouselLargeBundle(
      bundle = randomBundle,
      uiState = Idle(
        apps = listOf(randomApp, randomApp, randomApp)
      ),
      navigate = {},
    )
  }
}
