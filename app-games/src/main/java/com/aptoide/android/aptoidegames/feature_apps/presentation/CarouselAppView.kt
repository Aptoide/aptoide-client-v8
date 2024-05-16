package com.aptoide.android.aptoidegames.feature_apps.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.aptoide.android.aptoidegames.AptoideFeatureGraphicImage
import com.aptoide.android.aptoidegames.appview.buildAppViewRoute
import com.aptoide.android.aptoidegames.home.BundleHeader
import com.aptoide.android.aptoidegames.home.EmptyBundleView
import com.aptoide.android.aptoidegames.home.HorizontalPagerView
import com.aptoide.android.aptoidegames.home.LoadingBundleView
import com.aptoide.android.aptoidegames.home.getSeeMoreRouteNavigation
import com.aptoide.android.aptoidegames.installer.presentation.AppIconWProgress
import com.aptoide.android.aptoidegames.theme.AppGamesButton
import com.aptoide.android.aptoidegames.theme.AppTheme
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import com.aptoide.android.aptoidegames.theme.ButtonStyle.Default

@Composable
fun CarouselBundle(
  bundle: Bundle,
  navigate: (String) -> Unit,
) {
  val (uiState, _) = rememberAppsByTag(bundle.tag, bundle.timestamp)

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
fun CarouselListView(
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
fun CarouselAppView(
  app: App,
  onClick: () -> Unit,
) {

  Column(
    modifier = Modifier
      .clip(
        RoundedCornerShape(
          topStart = 16.dp,
          topEnd = 16.dp,
          bottomStart = 8.dp,
          bottomEnd = 8.dp
        )
      )
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
        .height(136.dp)
        .clip(RoundedCornerShape(16.dp)),
      data = app.featureGraphic,
      contentDescription = null,
    )
    Row(
      modifier = Modifier
        .height(48.dp)
        .padding(top = 8.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
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
            .clearAndSetSemantics { },
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          style = AppTheme.typography.headlineTitleText
        )
      }
      AppGamesButton(
        title = "Install",
        onClick = {},
        style = Default(fillWidth = false),
      )
    }
  }
}

@PreviewDark
@Composable
fun CarouselAppViewPreview() {
  AptoideTheme {
    CarouselAppView(
      app = randomApp,
      onClick = {}
    )
  }
}
