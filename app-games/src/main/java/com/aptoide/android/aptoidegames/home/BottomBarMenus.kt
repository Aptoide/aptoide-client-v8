package com.aptoide.android.aptoidegames.home

import androidx.compose.ui.graphics.vector.ImageVector
import com.aptoide.android.aptoidegames.categories.presentation.buildAllCategoriesRoute
import com.aptoide.android.aptoidegames.drawables.icons.getCategories
import com.aptoide.android.aptoidegames.drawables.icons.getDiscovery
import com.aptoide.android.aptoidegames.drawables.icons.getSearch
import com.aptoide.android.aptoidegames.search.presentation.searchRoute
import com.aptoide.android.aptoidegames.theme.greyLight

sealed class BottomBarMenus(
  val route: String,
  val title: String,
  val icon: ImageVector,
) {
  object Games : BottomBarMenus(
    route = gamesRoute,
    title = "Discovery", //TODO: hardcoded string
    icon = getDiscovery()
  )
  object Search : BottomBarMenus(
    route = searchRoute,
    title = "Search", //TODO: hardcoded string
    icon = getSearch(greyLight)
  )
  object Categories : BottomBarMenus(
    route = buildAllCategoriesRoute(),
    title = "Categories", //TODO: hardcoded string
    icon = getCategories()
  )
}
