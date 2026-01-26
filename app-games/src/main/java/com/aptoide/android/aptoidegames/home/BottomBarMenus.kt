package com.aptoide.android.aptoidegames.home

import android.graphics.Paint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.feature_updates.presentation.UpdatesUiState
import cm.aptoide.pt.feature_updates.presentation.rememberUpdates
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.analytics.dto.BundleMeta
import com.aptoide.android.aptoidegames.analytics.presentation.withBundleMeta
import com.aptoide.android.aptoidegames.categories.presentation.buildAllCategoriesRoute
import com.aptoide.android.aptoidegames.drawables.icons.getCategories
import com.aptoide.android.aptoidegames.drawables.icons.getDownloadIcon
import com.aptoide.android.aptoidegames.drawables.icons.getGamesIcon
import com.aptoide.android.aptoidegames.drawables.icons.getSearch
import com.aptoide.android.aptoidegames.gamegenie.presentation.composables.AnimationComposable
import com.aptoide.android.aptoidegames.gamegenie.presentation.genieRoute
import com.aptoide.android.aptoidegames.gamegenie.presentation.genieSearchRoute
import com.aptoide.android.aptoidegames.search.presentation.buildSearchRoute
import com.aptoide.android.aptoidegames.gamegenie.presentation.composables.getAiBottomBarIcon
import com.aptoide.android.aptoidegames.theme.Palette
import com.aptoide.android.aptoidegames.updates.presentation.updatesRoute

sealed class BottomBarMenus(
  val route: String,
  val titleId: Int,
) {
  object Games : BottomBarMenus(
    route = gamesRoute,
    titleId = R.string.games
  )

  object Search : BottomBarMenus(
    route = buildSearchRoute().withBundleMeta(BundleMeta("search", "app")),
    titleId = R.string.search
  )

  object GenieSearch : BottomBarMenus(
    route = genieSearchRoute.withBundleMeta(BundleMeta("geniesearch", "app")),
    titleId = R.string.search
  )

  object Categories : BottomBarMenus(
    route = buildAllCategoriesRoute()
      .withBundleMeta(BundleMeta("categories-more", "app")),
    titleId = R.string.categories
  )

  object Updates : BottomBarMenus(
    route = updatesRoute.withBundleMeta(BundleMeta("updates", "app")),
    titleId = R.string.bottom_navigation_updates
  )

  object GameGenie : BottomBarMenus(
    route = genieRoute.withBundleMeta(BundleMeta("gamegenie", "app")),
    titleId = R.string.genai_bottom_navigation_gamegenie_button
  )
}

@Composable
fun BottomBarMenus.Icon(isSelected: Boolean) = when (this) {
  BottomBarMenus.Games -> getGamesIcon(Palette.GreyLight).AsBottomBarIcon()
  BottomBarMenus.Search -> getSearch(Palette.GreyLight).AsBottomBarIcon()
  BottomBarMenus.GenieSearch -> getSearch(Palette.GreyLight).AsBottomBarIcon()
  BottomBarMenus.Categories -> getCategories(Palette.GreyLight).AsBottomBarIcon()
  BottomBarMenus.Updates -> {
    val updatesUiState = rememberUpdates()

    Box {
      getDownloadIcon(Palette.GreyLight).AsBottomBarIcon()
      if (updatesUiState is UpdatesUiState.Idle) {
        Box(
          modifier = Modifier
            .padding(top = 3.dp)
            .size(10.dp)
            .background(Palette.Error, CircleShape)
            .align(Alignment.TopEnd)
        )
      }
    }
  }

  BottomBarMenus.GameGenie -> {
    Box(
      modifier = Modifier
        .size(58.dp)
        .drawBehind {
          val shadowColor = if (isSelected) Palette.Primary.copy(alpha = 0.4f).toArgb() else Color.Transparent.toArgb()
          val paint = Paint().apply {
            this.setShadowLayer(
              24.dp.toPx(),
              0f,
              0f,
              shadowColor
            )
          }
          drawIntoCanvas { canvas ->
            canvas.nativeCanvas.drawCircle(
              center.x,
              center.y,
              size.minDimension / 2f,
              paint
            )
          }
        }
        .clip(CircleShape)
        .background(if (isSelected) Palette.GameGenieGrey else Palette.GreyDark),
      contentAlignment = Alignment.Center
    ) {
      AnimationComposable(
        modifier = Modifier.size(58.dp),
        isSelected = isSelected,
        resId = R.raw.game_genie_bottom_bar_idle,
        selectedResId = R.raw.game_genie_chat_big_animation
      )
      Icon(
        imageVector = getAiBottomBarIcon(),
        contentDescription = null,
        modifier = Modifier
          .align(Alignment.BottomEnd)
          .offset(x = (-10).dp, y = (-10).dp)
          .size(14.dp),
        tint = Color.Unspecified
      )
    }
  }
}

@Composable
private fun ImageVector.AsBottomBarIcon() = Icon(
  imageVector = this,
  contentDescription = null,
)
