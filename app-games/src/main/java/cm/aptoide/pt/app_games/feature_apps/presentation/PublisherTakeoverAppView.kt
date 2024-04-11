package cm.aptoide.pt.app_games.feature_apps.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.app_games.AptoideAsyncImage
import cm.aptoide.pt.app_games.AptoideFeatureGraphicImage
import cm.aptoide.pt.app_games.appview.buildAppViewRoute
import cm.aptoide.pt.app_games.home.HorizontalPagerView
import cm.aptoide.pt.app_games.home.LoadingBundleView
import cm.aptoide.pt.app_games.home.SeeMoreView
import cm.aptoide.pt.app_games.home.translateOrKeep
import cm.aptoide.pt.app_games.installer.AppIconWProgress
import cm.aptoide.pt.app_games.theme.AppGamesButton
import cm.aptoide.pt.app_games.theme.AppTheme
import cm.aptoide.pt.app_games.theme.ButtonStyle.Default
import cm.aptoide.pt.app_games.theme.pureBlack
import cm.aptoide.pt.app_games.theme.pureWhite
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.presentation.AppsListUiState.Empty
import cm.aptoide.pt.feature_apps.presentation.AppsListUiState.Error
import cm.aptoide.pt.feature_apps.presentation.AppsListUiState.Idle
import cm.aptoide.pt.feature_apps.presentation.AppsListUiState.Loading
import cm.aptoide.pt.feature_apps.presentation.AppsListUiState.NoConnection
import cm.aptoide.pt.feature_apps.presentation.rememberAppsByTag
import cm.aptoide.pt.feature_home.domain.Bundle

@Composable
fun PublisherTakeover(
  bundle: Bundle,
  navigate: (String) -> Unit,
) {
  val (uiState, _) = rememberAppsByTag(bundle.tag, bundle.timestamp)
  val (bottomUiState, _) = rememberAppsByTag(bundle.bottomTag ?: "", bundle.timestamp)

  Box {
    AptoideAsyncImage(
      modifier = Modifier
        .matchParentSize(),
      data = bundle.background,
      contentDescription = null
    )
    Column(
      modifier = Modifier
        .background(color = pureBlack.copy(0.7f))
        .padding(bottom = 24.dp)
    ) {
      Row(
        modifier = Modifier
          .wrapContentHeight()
          .fillMaxWidth()
          .padding(start = 24.dp, top = 16.dp, end = 16.dp, bottom = 8.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        AptoideAsyncImage(
          modifier = Modifier
            .size(64.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(color = Color.Transparent),
          data = bundle.bundleIcon,
          contentDescription = null,
        )
        if (bundle.hasMoreAction) {
          SeeMoreView(
            actionColor = pureWhite,
            modifier = Modifier.padding(top = 4.dp)
          )
        }
      }
      Text(
        text = bundle.title.translateOrKeep(LocalContext.current),
        modifier = Modifier
          .semantics { heading() }
          .padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
        overflow = TextOverflow.Ellipsis,
        maxLines = 2,
        color = pureWhite,
        style = AppTheme.typography.headlineTitleText
      )
      when (uiState) {
        is Idle -> PublisherTakeOverListView(
          appsList = uiState.apps,
          navigate = navigate,
        )

        Empty,
        Error,
        NoConnection,
        -> { /*nothing to show*/
        }

        Loading -> LoadingBundleView(height = 184.dp)
      }
      when (bottomUiState) {
        is Idle -> AppsRowView(
          appsList = bottomUiState.apps,
          navigate = navigate,
          appsNameColor = Color.White,
        )

        Empty,
        Error,
        NoConnection,
        -> { /*nothing to show*/
        }

        Loading -> LoadingBundleView(height = 184.dp)
      }
    }
  }
}

@Composable
fun PublisherTakeOverListView(
  appsList: List<App>,
  navigate: (String) -> Unit,
) {

  HorizontalPagerView(
    appsList = appsList,
    modifier = Modifier.padding(bottom = 24.dp)
  ) { modifier, page, app ->
    Box(
      modifier
        .width(280.dp)
        .height(184.dp)
        .background(color = Color.Transparent)
    ) {
      Column(
        modifier = Modifier
          .semantics(mergeDescendants = true) { }
          .clickable(onClick = {
            navigate(
              buildAppViewRoute(app.packageName)
            )
          }
          )
      ) {
        AptoideFeatureGraphicImage(
          modifier = Modifier
            .padding(bottom = 8.dp)
            .width(280.dp)
            .height(136.dp)
            .clip(RoundedCornerShape(16.dp)),
          data = app.featureGraphic,
          contentDescription = null,
        )
        Row(
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
              modifier = Modifier.wrapContentHeight(),
              maxLines = 1,
              color = pureWhite,
              overflow = TextOverflow.Ellipsis,
              style = AppTheme.typography.gameTitleTextCondensed
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
  }
}
