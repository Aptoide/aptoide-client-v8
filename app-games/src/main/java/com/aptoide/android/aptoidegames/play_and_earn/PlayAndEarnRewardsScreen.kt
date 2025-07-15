package com.aptoide.android.aptoidegames.play_and_earn

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.extensions.ScreenData
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.randomAppWithImages
import cm.aptoide.pt.feature_apps.domain.AppSource
import com.aptoide.android.aptoidegames.AptoideFeatureGraphicImage
import com.aptoide.android.aptoidegames.analytics.presentation.withAnalytics
import com.aptoide.android.aptoidegames.appview.buildAppViewRoute
import com.aptoide.android.aptoidegames.drawables.icons.play_and_earn.getSmallCoinIcon
import com.aptoide.android.aptoidegames.drawables.icons.play_and_earn.getThumbUpIcon
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette
import kotlin.random.Random

const val playAndEarnRewardsRoute = "playAndEarnRewards"

fun playAndEarnRewardsScreen() = ScreenData.withAnalytics(
  route = playAndEarnRewardsRoute,
  screenAnalyticsName = "PlayAndEarnRewards",
) { _, navigate, _ ->

  PlayAndEarnRewardsScreen(navigate)
}

@Composable
fun PlayAndEarnRewardsScreen(
  navigate: (String) -> Unit
) {
  val scrollState = rememberScrollState()
  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(scrollState),
    verticalArrangement = Arrangement.spacedBy(8.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    PlayAndEarnHowItWorksSection()
    PlayAndEarnLetsGoCard(
      modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
      onLetsGoClick = { navigate(playAndEarnLoginRoute) }
    )
    PlayAndEarnFavouritesList(navigate)
  }
}

@Composable
fun PlayAndEarnFavouritesList(
  navigate: (String) -> Unit
) {
  val apps = remember { List(3) { randomAppWithImages } }

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(all = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    PlayAndEarnSectionHeader(
      icon = getThumbUpIcon(),
      text = "Everyone’s favourites"
    )
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      repeat(apps.size) {
        PlayAndEarnFavouriteItem(
          app = apps[it],
          onClick = {
            navigate(
              buildAppViewRoute(AppSource.of(packageName = "com.mobile.legends", appId = null))
            )
          }
        )
      }
    }
  }
}

@Composable
fun PlayAndEarnFavouriteItem(
  app: App,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  Column(
    modifier = modifier.clickable(onClick = onClick)
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(328f / 160f)
        .border(width = 2.dp, color = Palette.Yellow)
    ) {
      AptoideFeatureGraphicImage(
        modifier = Modifier.matchParentSize(),
        data = app.featureGraphic,
        contentDescription = null,
      )
      Row(
        modifier = Modifier
          .wrapContentWidth()
          .padding(all = 16.dp)
          .align(Alignment.BottomStart),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(
          modifier = Modifier
            .weight(1f)
            .padding(end = 16.dp),
          verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Text(
            text = "lorem ahaha lorem ahah ipsum ipsum lorem",
            style = AGTypography.DescriptionGames,
            color = Palette.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )

          AppXPText(20, 50)
        }

        Button(
          onClick = onClick,
          modifier = modifier,
          enabled = true,
          elevation = ButtonDefaults.elevation(defaultElevation = 0.dp),
          shape = CutCornerShape(0),
          colors = ButtonDefaults.buttonColors(
            backgroundColor = Palette.Secondary,
            disabledBackgroundColor = Palette.Grey,
          ),
          contentPadding = PaddingValues(horizontal = 16.dp),
        ) {
          Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Text(
              text = "Install",
              maxLines = 1,
              textAlign = TextAlign.Center,
              style = AGTypography.InputsS,
              color = Palette.White
            )
            Icon(
              imageVector = getSmallCoinIcon(),
              contentDescription = null,
              tint = Color.Unspecified
            )
          }

        }

        /*
        AccentSmallButton(
          modifier = Modifier,
          title = "Install",
          onClick = {}
        )

         */
      }
    }
    ProgressIndicator(
      progress = Random.nextDouble(0.2, 0.8).toFloat(),
      modifier = Modifier.fillMaxWidth()
    )
  }
}

@Preview
@Composable
fun PlayAndEarnRewardsScreenPreview() {
  PlayAndEarnRewardsScreen(
    navigate = {}
  )
}
