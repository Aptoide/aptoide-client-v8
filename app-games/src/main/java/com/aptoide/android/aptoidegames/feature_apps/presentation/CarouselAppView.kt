package com.aptoide.android.aptoidegames.feature_apps.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.extensions.PreviewDark
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.randomApp
import cm.aptoide.pt.feature_apps.presentation.AppsListUiState
import cm.aptoide.pt.feature_apps.presentation.rememberAppsByTag
import cm.aptoide.pt.feature_home.domain.Bundle
import cm.aptoide.pt.feature_home.domain.randomBundle
import com.aptoide.android.aptoidegames.AptoideFeatureGraphicImage
import com.aptoide.android.aptoidegames.appview.buildAppViewRoute
import com.aptoide.android.aptoidegames.home.BundleHeader
import com.aptoide.android.aptoidegames.home.EmptyBundleView
import com.aptoide.android.aptoidegames.home.HorizontalPagerView
import com.aptoide.android.aptoidegames.home.LoadingBundleView
import com.aptoide.android.aptoidegames.home.getSeeMoreRouteNavigation
import com.aptoide.android.aptoidegames.installer.presentation.AppIconWProgress
import com.aptoide.android.aptoidegames.installer.presentation.InstallViewShort
import com.aptoide.android.aptoidegames.installer.presentation.ProgressText
import com.aptoide.android.aptoidegames.theme.AppTheme
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import com.aptoide.android.aptoidegames.theme.pureWhite

@Composable
fun CarouselBundle(
  bundle: Bundle,
  navigate: (String) -> Unit,
) {
  val (uiState, _) = rememberAppsByTag(bundle.tag, bundle.timestamp)

  RealCarouselBundle(
    bundle = bundle,
    uiState = uiState,
    navigate = navigate
  )
}

@Composable
private fun RealCarouselBundle(
  bundle: Bundle,
  uiState: AppsListUiState,
  navigate: (String) -> Unit
) {
  Column(
    modifier = Modifier.padding(bottom = 16.dp)
  ) {
    BundleHeader(
      title = bundle.title,
      icon = bundle.bundleIcon,
      hasMoreAction = bundle.hasMoreAction,
      onClick = getSeeMoreRouteNavigation(bundle = bundle, navigate = navigate),
    )
    when (uiState) {
      is AppsListUiState.Idle -> CarouselListView(
        appsList = uiState.apps,
        navigate = navigate
      )

      AppsListUiState.Empty,
      AppsListUiState.Error,
      AppsListUiState.NoConnection,
      -> EmptyBundleView(height = 184.dp)

      AppsListUiState.Loading -> LoadingBundleView(height = 184.dp)
    }
  }
}

@Composable
private fun CarouselListView(
  appsList: List<App>,
  navigate: (String) -> Unit,
) {
  HorizontalPagerView(appsList = appsList) { modifier, page, item ->
    Box(
      modifier
        .width(280.dp)
        .height(184.dp)
        .background(color = Color.Transparent)
    ) {
      CarouselAppView(
        app = item,
        onClick = {
          navigate(
            buildAppViewRoute(item.packageName)
          )
        }
      )
    }
  }
}

@Composable
private fun CarouselAppView(
  app: App,
  onClick: () -> Unit,
) {
  Column(
    modifier = Modifier
      .requiredWidth(280.dp)
      .height(184.dp)
      .semantics(mergeDescendants = true) {
        contentDescription = app.name
      }
      .clickable(onClick = onClick)
  ) {
    AptoideFeatureGraphicImage(
      modifier = Modifier
        .width(280.dp)
        .height(136.dp),
      data = app.featureGraphic,
      contentDescription = null,
    )
    Row(
      modifier = Modifier.padding(top = 8.dp),
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
            .weight(1f)
            .clearAndSetSemantics { },
          color = pureWhite,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis,
          style = AppTheme.typography.descriptionGames
        )
        ProgressText(app = app, showVersionName = false)
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
private fun RealCarouselBundlePreview() {
  AptoideTheme {
    RealCarouselBundle(
      bundle = randomBundle,
      uiState = AppsListUiState.Idle(
        apps = listOf(randomApp, randomApp, randomApp),
      ),
      navigate = {}
    )
  }
}

@PreviewAll
@Composable
private fun CarouselListViewPreview() {
  AptoideTheme {
    CarouselListView(
      appsList = listOf(randomApp, randomApp, randomApp),
      navigate = {}
    )
  }
}

@PreviewAll
@Composable
private fun CarouselAppViewPreview() {
  AptoideTheme {
    CarouselAppView(
      app = randomApp,
      onClick = {}
    )
  }
}
