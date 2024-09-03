package com.aptoide.android.aptoidegames.home

import androidx.compose.ui.graphics.vector.ImageVector
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.analytics.dto.BundleMeta
import com.aptoide.android.aptoidegames.analytics.presentation.withBundleMeta
import com.aptoide.android.aptoidegames.categories.presentation.buildAllCategoriesRoute
import com.aptoide.android.aptoidegames.chatbot.presentation.chatbotRoute
import com.aptoide.android.aptoidegames.drawables.icons.getCategories
import com.aptoide.android.aptoidegames.drawables.icons.getDiscovery
import com.aptoide.android.aptoidegames.drawables.icons.getSearch
import com.aptoide.android.aptoidegames.search.presentation.searchRoute
import com.aptoide.android.aptoidegames.theme.Palette

sealed class BottomBarMenus(
  val route: String,
  val titleId: Int,
  val icon: ImageVector,
) {
  object Games : BottomBarMenus(
    route = gamesRoute,
    titleId = R.string.bottom_navigation_discovery,
    icon = getDiscovery(Palette.GreyLight)
  )

  object Search : BottomBarMenus(
    route = searchRoute
      .withBundleMeta(BundleMeta("search", "app")),
    titleId = R.string.search,
    icon = getSearch(Palette.GreyLight)
  )

  object Categories : BottomBarMenus(
    route = buildAllCategoriesRoute()
      .withBundleMeta(BundleMeta("categories-more", "app")),
    titleId = R.string.categories,
    icon = getCategories(Palette.GreyLight)
  )

  object Chatbot : BottomBarMenus(
    route = chatbotRoute,
    titleId = R.string.chatbot,
    icon = getSearch(Palette.GreyLight)
  )
}
