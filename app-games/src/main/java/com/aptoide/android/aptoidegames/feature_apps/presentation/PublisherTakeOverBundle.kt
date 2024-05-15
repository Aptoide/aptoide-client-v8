package com.aptoide.android.aptoidegames.feature_apps.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.extensions.PreviewAll
import cm.aptoide.pt.feature_apps.data.App
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
import com.aptoide.android.aptoidegames.home.HorizontalPagerView
import com.aptoide.android.aptoidegames.home.LoadingBundleView
import com.aptoide.android.aptoidegames.home.SeeMoreView
import com.aptoide.android.aptoidegames.home.getSeeMoreRouteNavigation
import com.aptoide.android.aptoidegames.home.translateOrKeep
import com.aptoide.android.aptoidegames.installer.presentation.AppIconWProgress
import com.aptoide.android.aptoidegames.theme.AppGamesButton
import com.aptoide.android.aptoidegames.theme.AppTheme
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import com.aptoide.android.aptoidegames.theme.ButtonStyle.Default
import com.aptoide.android.aptoidegames.theme.pureBlack
import com.aptoide.android.aptoidegames.theme.pureWhite

@Composable
fun PublisherTakeOverBundle(
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
            onClick = getSeeMoreRouteNavigation(bundle = bundle, navigate = navigate),
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
@PreviewAll
@Composable
fun PublisherTakeOverBundlePreview(
  @PreviewParameter(AppsListUiStateProvider::class) uiState: AppsListUiState,
) {
  AptoideTheme(darkTheme = isSystemInDarkTheme()) {
    PublisherTakeOverBundle(
      bundle = randomBundle,
      navigate = {},
    )
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
            modifier = Modifier.size(40.dp),
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
