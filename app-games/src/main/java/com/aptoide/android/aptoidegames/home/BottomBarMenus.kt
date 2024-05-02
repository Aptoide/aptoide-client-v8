package com.aptoide.android.aptoidegames.home

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.SportsEsports
import androidx.compose.ui.graphics.vector.ImageVector
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.search.presentation.searchRoute
import com.aptoide.android.aptoidegames.categories.presentation.buildAllCategoriesRoute

sealed class BottomBarMenus(
  val route: String,
  @StringRes val resourceId: Int,
  val icon: ImageVector,
) {
  object Games : BottomBarMenus(gamesRoute, R.string.games, Icons.Outlined.SportsEsports)
  object Search : BottomBarMenus(searchRoute, R.string.search, Icons.Outlined.Search)
  object Categories : BottomBarMenus(
    buildAllCategoriesRoute(),
    R.string.categories, Icons.Outlined.Category
  )
}
