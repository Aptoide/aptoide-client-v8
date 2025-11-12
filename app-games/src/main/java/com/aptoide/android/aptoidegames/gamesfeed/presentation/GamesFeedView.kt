package com.aptoide.android.aptoidegames.gamesfeed.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.navDeepLink
import cm.aptoide.pt.aptoide_ui.textformatter.DateUtils
import cm.aptoide.pt.extensions.ScreenData
import com.aptoide.android.aptoidegames.AptoideAsyncImage
import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.analytics.presentation.withAnalytics
import com.aptoide.android.aptoidegames.drawables.icons.playCircleIcon
import com.aptoide.android.aptoidegames.error_views.GenericErrorView
import com.aptoide.android.aptoidegames.gamesfeed.analytics.rememberGamesFeedAnalytics
import com.aptoide.android.aptoidegames.gamesfeed.repository.GamesFeedItem
import com.aptoide.android.aptoidegames.gamesfeed.repository.GamesFeedItemType
import com.aptoide.android.aptoidegames.home.LoadingView
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette

private const val gamesFeedScreenRoute = "gamesFeed"

fun gamesFeedScreen() = ScreenData.withAnalytics(
  route = gamesFeedScreenRoute,
  screenAnalyticsName = "GamesFeed",
  arguments = listOf(),
  deepLinks = listOf(navDeepLink {
    uriPattern = BuildConfig.DEEP_LINK_SCHEMA + gamesFeedScreenRoute
  }),
) { _, _, navigateBack ->
  GamesFeedView()
}

fun buildGamesFeedRoute() = gamesFeedScreenRoute

@Composable
fun GamesFeedView(
) {
  val (uiState, loadGamesFeed) = rememberGamesFeedViewModel()

  when (uiState) {
    is GamesFeedUiState.Loading -> LoadingView()
    is GamesFeedUiState.Empty,
    is GamesFeedUiState.NoConnection,
    is GamesFeedUiState.Error -> GenericErrorView(
      onRetryClick = { loadGamesFeed() }
    )

    is GamesFeedUiState.Idle -> GamesFeedContent(
      items = uiState.items,
      bundleIcon = uiState.bundleIcon
    )
  }
}

@Composable
private fun GamesFeedContent(
  items: List<GamesFeedItem>,
  bundleIcon: String?
) {
  val context = LocalContext.current
  val gamesFeedAnalytics = rememberGamesFeedAnalytics()

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    contentPadding = PaddingValues(bottom = 24.dp)
  ) {
    item {
      Text(
        text = stringResource(R.string.gamesfeed_detail_header),
        style = AGTypography.SubHeadingM,
        color = Palette.White,
        textAlign = TextAlign.Center,
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 8.dp)
      )
    }

    item {
      RobloxIconDecoration(bundleIcon = bundleIcon)
    }

    items(items) { item ->
      GamesFeedPost(
        item = item,
        onClick = {
          item.url?.let { url ->
            context.openUrlExternally(url)
            gamesFeedAnalytics.sendGamesFeedItemClick(item.title, url, "feed")
          }
        }
      )
    }
  }
}

@Composable
private fun GamesFeedPost(
  item: GamesFeedItem,
  onClick: () -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(bottom = 16.dp)
      .clickable(onClick = onClick)
  ) {
    if (item.type == GamesFeedItemType.VIDEO) {
      VideoFeatureGraphic(item)
    } else {
      AptoideAsyncImage(
        data = item.featureGraphic,
        contentDescription = item.title,
        modifier = Modifier.height(160.dp),
        contentScale = ContentScale.Crop
      )
    }
    Spacer(modifier = Modifier.height(8.dp))

    Column {
      Text(
        text = item.title,
        style = AGTypography.InputsL,
        color = Palette.White,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
      )

      Spacer(modifier = Modifier.height(4.dp))

      if (item.type == GamesFeedItemType.ARTICLE) {
        item.description?.takeIf { it.isNotBlank() }?.let { description ->
          Text(
            text = description,
            style = AGTypography.SmallGames,
            color = Palette.White,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
          )
        }
        Spacer(modifier = Modifier.height(8.dp))
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        AuthorRow(authorLogo = item.authorLogo, authorName = item.authorName)

        item.publishedAt?.let { timestamp ->
          Text(
            text = DateUtils.getTimeDiffString(LocalContext.current, timestamp),
            style = AGTypography.SmallGames,
            color = Palette.GreyLight
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    Divider(
      color = Palette.GreyDark,
      thickness = 1.dp
    )
  }
}

@Composable
private fun VideoFeatureGraphic(item: GamesFeedItem) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .height(195.dp)
  ) {
    AptoideAsyncImage(
      data = item.featureGraphic,
      contentDescription = item.title,
      modifier = Modifier.matchParentSize(),
      contentScale = ContentScale.Crop
    )
    Box(
      modifier = Modifier
        .matchParentSize()
        .background(Color.Black.copy(alpha = 0.3f))
    )
    Image(
      imageVector = playCircleIcon(
        color = Palette.White
      ),
      contentDescription = "Play Video",
      modifier = Modifier
        .size(48.dp)
        .align(Alignment.Center),
    )
  }
}

@Composable
private fun ArticleCard(
  item: GamesFeedItem,
  onClick: () -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp)
      .clickable(onClick = onClick)
  ) {
    AptoideAsyncImage(
      data = item.featureGraphic,
      contentDescription = item.title,
      modifier = Modifier.height(160.dp),
      contentScale = ContentScale.Crop
    )

    Spacer(modifier = Modifier.height(8.dp))

    Column {
      Text(
        text = item.title,
        style = AGTypography.InputsL,
        color = Palette.White,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
      )
      item.description?.takeIf { it.isNotBlank() }?.let { description ->
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = description,
          style = AGTypography.SmallGames,
          color = Palette.White,
          maxLines = 3,
          overflow = TextOverflow.Ellipsis
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        AuthorRow(item.authorLogo, item.authorName)

        item.publishedAt?.let { timestamp ->
          Text(
            text = DateUtils.getTimeDiffString(LocalContext.current, timestamp),
            style = AGTypography.SmallGames,
            color = Palette.GreyLight
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    Divider(
      color = Palette.GreyDark,
      thickness = 1.dp
    )
  }
}

@Composable
private fun RobloxIconDecoration(bundleIcon: String?) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .padding(bottom = 24.dp),
    contentAlignment = Alignment.Center
  ) {
    AptoideAsyncImage(
      data = bundleIcon ?: R.drawable.roblox,
      contentDescription = null,
      modifier = Modifier.size(72.dp)
    )
    AptoideAsyncImage(
      data = R.drawable.gamesfeed_app_border,
      contentDescription = null,
      modifier = Modifier.size(width = 152.dp, height = 80.dp)
    )
  }
}
