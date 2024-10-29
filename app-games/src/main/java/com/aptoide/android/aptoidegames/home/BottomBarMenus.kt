package com.aptoide.android.aptoidegames.home

import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.analytics.dto.BundleMeta
import com.aptoide.android.aptoidegames.analytics.presentation.withBundleMeta
import com.aptoide.android.aptoidegames.categories.presentation.buildAllCategoriesRoute
import com.aptoide.android.aptoidegames.drawables.icons.getCategories
import com.aptoide.android.aptoidegames.drawables.icons.getDiscovery
import com.aptoide.android.aptoidegames.drawables.icons.getSearch
import com.aptoide.android.aptoidegames.search.presentation.searchRoute
import com.aptoide.android.aptoidegames.theme.Palette

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
}

@Composable
fun BottomBarMenus.Icon() = when (this) {
  BottomBarMenus.Games -> getDiscovery(Palette.GreyLight).AsBottomBarIcon()
  BottomBarMenus.Search -> getSearch(Palette.GreyLight).AsBottomBarIcon()
  BottomBarMenus.Categories -> getCategories(Palette.GreyLight).AsBottomBarIcon()
}

@Composable
private fun ImageVector.AsBottomBarIcon() = Icon(
  imageVector = this,
  contentDescription = null,
)
