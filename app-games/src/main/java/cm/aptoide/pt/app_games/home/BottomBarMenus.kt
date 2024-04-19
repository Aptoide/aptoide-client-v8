package cm.aptoide.pt.app_games.home

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.SportsEsports
import androidx.compose.ui.graphics.vector.ImageVector
import cm.aptoide.pt.app_games.R
import cm.aptoide.pt.app_games.search.presentation.searchRoute
import cm.aptoide.pt.app_games.categories.presentation.buildAllCategoriesRoute

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
