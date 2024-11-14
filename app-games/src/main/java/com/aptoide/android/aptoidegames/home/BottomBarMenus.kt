package com.aptoide.android.aptoidegames.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.feature_updates.presentation.UpdatesUiState
import cm.aptoide.pt.feature_updates.presentation.rememberUpdates
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.analytics.dto.BundleMeta
import com.aptoide.android.aptoidegames.analytics.presentation.withBundleMeta
import com.aptoide.android.aptoidegames.categories.presentation.buildAllCategoriesRoute
import com.aptoide.android.aptoidegames.gamegenie.presentation.genieRoute
import com.aptoide.android.aptoidegames.drawables.icons.getCategories
import com.aptoide.android.aptoidegames.drawables.icons.getDiscovery
import com.aptoide.android.aptoidegames.drawables.icons.getDownloadIcon
import com.aptoide.android.aptoidegames.drawables.icons.getSearch
import com.aptoide.android.aptoidegames.search.presentation.searchRoute
import com.aptoide.android.aptoidegames.theme.Palette
import com.aptoide.android.aptoidegames.updates.presentation.updatesRoute

sealed class BottomBarMenus(
  val route: String,
  val titleId: Int,
) {
  object Games : BottomBarMenus(
    route = gamesRoute,
    titleId = R.string.bottom_navigation_discovery
  )

  object Search : BottomBarMenus(
    route = searchRoute
      .withBundleMeta(BundleMeta("search", "app")),
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
    route = genieRoute,
    titleId = R.string.bottom_navigation_updates
  )
}

@Composable
fun BottomBarMenus.Icon() = when (this) {
  BottomBarMenus.Games -> getDiscovery(Palette.GreyLight).AsBottomBarIcon()
  BottomBarMenus.Search -> getSearch(Palette.GreyLight).AsBottomBarIcon()
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
  BottomBarMenus.GameGenie -> getDiscovery(Palette.GreyLight).AsBottomBarIcon()
}

@Composable
private fun ImageVector.AsBottomBarIcon() = Icon(
  imageVector = this,
  contentDescription = null,
)
