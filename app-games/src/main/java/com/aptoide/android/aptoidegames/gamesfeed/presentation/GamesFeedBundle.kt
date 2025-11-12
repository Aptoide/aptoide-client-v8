package com.aptoide.android.aptoidegames.gamesfeed.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.feature_home.domain.Bundle
import com.aptoide.android.aptoidegames.AptoideAsyncImage
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.drawables.icons.playCircleIcon
import com.aptoide.android.aptoidegames.gamesfeed.analytics.rememberGamesFeedAnalytics
import com.aptoide.android.aptoidegames.gamesfeed.repository.GamesFeedItem
import com.aptoide.android.aptoidegames.gamesfeed.repository.GamesFeedItemType
import com.aptoide.android.aptoidegames.home.LoadingBundleView
import com.aptoide.android.aptoidegames.home.SeeMoreView
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette

@Composable
fun GamesFeedBundle(
  bundle: Bundle,
  navigate: (String) -> Unit,
  spaceBy: Int = 0,
) {
  val (uiState, _) = rememberGamesFeedViewModel()
  when (uiState) {
    is GamesFeedUiState.Loading -> LoadingBundleView(height = 200.dp)
    is GamesFeedUiState.Idle -> {
      val items = uiState.items.take(3)
      if (items.isNotEmpty()) {
        GamesFeedBundleContent(
          bundle = bundle,
          items = items,
          navigate = navigate,
          bundleGraphic = uiState.bundleGraphic,
          bundleIcon = uiState.bundleIcon,
          spaceBy = spaceBy
        )
      }
    }

    is GamesFeedUiState.Empty,
    is GamesFeedUiState.Error,
    is GamesFeedUiState.NoConnection -> {
    }
  }
}

@Composable
private fun GamesFeedBundleContent(
  bundle: Bundle,
  items: List<GamesFeedItem>,
  navigate: (String) -> Unit,
  bundleGraphic: String? = null,
  bundleIcon: String? = null,
  spaceBy: Int
) {
  val context = LocalContext.current
  val gamesFeedAnalytics = rememberGamesFeedAnalytics()

  Column {
    Box(
      modifier = Modifier.fillMaxWidth()
    ) {
      AptoideAsyncImage(
        data = bundleGraphic ?: R.drawable.roblox_background,
        contentDescription = null,
        modifier = Modifier
          .fillMaxWidth()
          .height(330.dp)
          .alpha(0.2f)
      )

      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 24.dp)
      ) {
        GamesFeedHeader(
          title = bundle.title,
          bundleIcon = bundleIcon,
          onSeeMoreClick = {
            gamesFeedAnalytics.sendGamesFeedSeeAllClick()
            navigate(buildGamesFeedRoute())
          }
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(16.dp),
          contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp)
        ) {
          items(items) { item ->
            GamesFeedPost(
              item = item,
              onClick = {
                item.url?.let { url ->
                  context.openUrlExternally(url)
                  gamesFeedAnalytics.sendGamesFeedItemClick(item.title, url, "home")
                }
              }
            )
          }
        }
      }
    }
    Spacer(modifier = Modifier.size(spaceBy.dp))
  }
}

@Composable
private fun GamesFeedHeader(
  title: String,
  bundleIcon: String?,
  onSeeMoreClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp),
    verticalAlignment = Alignment.Top,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(16.dp),
      modifier = Modifier.weight(1f, fill = false)
    ) {
      AptoideAsyncImage(
        data = bundleIcon ?: R.drawable.roblox,
        contentDescription = null,
        modifier = Modifier
          .size(48.dp)
      )

      Column {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Image(
            painter = painterResource(id = R.drawable.ic_wand_stars),
            contentDescription = null,
            modifier = Modifier.size(16.dp)
          )
          Text(
            text = stringResource(R.string.gamesfeed_bundle_header_1),
            style = AGTypography.ChatBold,
            color = Palette.Primary
          )
        }

        Spacer(modifier = Modifier.size(2.dp))

        Text(
          text = title,
          style = AGTypography.Title,
          color = Palette.White,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }
    }
    SeeMoreView(onClick = onSeeMoreClick)
  }
}

@Composable
private fun GamesFeedPost(
  item: GamesFeedItem,
  onClick: () -> Unit
) {
  Column(
    modifier = Modifier
      .width(280.dp)
      .height(218.dp)
      .clickable(onClick = onClick)
  ) {
    if (item.type == GamesFeedItemType.VIDEO) {
      VideoFeatureGraphic(item)
    } else {
      AptoideAsyncImage(
        data = item.featureGraphic,
        contentDescription = item.title,
        modifier = Modifier.height(136.dp)
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
      AuthorRow(item.authorLogo, item.authorName)
    }
  }
}

@Composable
private fun VideoFeatureGraphic(item: GamesFeedItem) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .height(136.dp)
  ) {
    AptoideAsyncImage(
      data = item.featureGraphic,
      contentDescription = item.title,
      modifier = Modifier.matchParentSize()
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
      contentDescription = "Play video",
      modifier = Modifier
        .size(40.dp)
        .align(Alignment.Center)
    )
  }
}

@Composable
fun AuthorRow(authorLogo: String?, authorName: String?) {
  Row(
    horizontalArrangement = Arrangement.spacedBy(2.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    authorLogo?.let { logo ->
      AptoideAsyncImage(
        data = logo,
        contentDescription = null,
        modifier = Modifier.size(16.dp)
      )
    }

    authorName?.let { author ->
      Text(
        text = author,
        style = AGTypography.SmallGames,
        color = Palette.White
      )
    }
  }
}
