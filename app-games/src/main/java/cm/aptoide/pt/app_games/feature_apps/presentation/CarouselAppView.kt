package cm.aptoide.pt.app_games.feature_apps.presentation

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
import cm.aptoide.pt.app_games.AptoideFeatureGraphicImage
import cm.aptoide.pt.app_games.home.BundleHeader
import cm.aptoide.pt.app_games.home.EmptyBundleView
import cm.aptoide.pt.app_games.home.HorizontalPagerView
import cm.aptoide.pt.app_games.home.LoadingBundleView
import cm.aptoide.pt.app_games.installer.AppIconWProgress
import cm.aptoide.pt.app_games.theme.AppGamesButton
import cm.aptoide.pt.app_games.theme.AppTheme
import cm.aptoide.pt.app_games.theme.AptoideTheme
import cm.aptoide.pt.app_games.theme.ButtonStyle.Default
import cm.aptoide.pt.extensions.PreviewAll
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.randomApp
import cm.aptoide.pt.feature_apps.presentation.AppsListUiState
import cm.aptoide.pt.feature_apps.presentation.tagApps
import cm.aptoide.pt.feature_home.domain.Bundle

@Composable
fun CarouselBundle(
  bundle: Bundle,
  navigate: (String) -> Unit,
) {
  val (uiState, _) = tagApps(bundle.tag, bundle.timestamp)

  Column(
    modifier = Modifier.padding(bottom = 16.dp)
  ) {
    BundleHeader(
      bundle = bundle,
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
  navigate: (String) -> Unit
) {

  HorizontalPagerView(appsList = appsList) { modifier, page, app ->
    Box(
      modifier
        .width(280.dp)
        .height(184.dp)
        .background(color = Color.Transparent)
    ) {
      CarouselAppView(
        app = app,
        onClick = {}
      )
    }
  }
}

@Composable
fun CarouselAppView(
  app: App,
  onClick: () -> Unit
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
        progressCornerRadius = 8f / 40,
        modifier = Modifier
          .size(40.dp)
          .clip(RoundedCornerShape(8.dp)),
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

@PreviewAll
@Composable
fun CarouselAppViewPreview() {
  AptoideTheme {
    CarouselAppView(
      app = randomApp,
      onClick = {}
    )
  }
}
