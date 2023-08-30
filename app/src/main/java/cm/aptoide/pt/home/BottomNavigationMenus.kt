package cm.aptoide.pt.home

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EuroSymbol
import androidx.compose.material.icons.outlined.FilterNone
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.SportsEsports
import androidx.compose.ui.graphics.vector.ImageVector
import cm.aptoide.pt.R
import cm.aptoide.pt.search.presentation.searchRoute
import cm.aptoide.pt.updates.updatesRoute

sealed class BottomNavigationMenus(
  val route: String,
  @StringRes val resourceId: Int,
  val icon: ImageVector,
) {
  object Games : BottomNavigationMenus(gamesRoute, R.string.games, Icons.Outlined.SportsEsports)
  object Apps : BottomNavigationMenus(appsRoute, R.string.apps, Icons.Outlined.GridView)
  object AppCoins : BottomNavigationMenus(bonusRoute, R.string.bonus, Icons.Outlined.EuroSymbol)
  object Search : BottomNavigationMenus(searchRoute, R.string.search, Icons.Outlined.Search)
  object Updates : BottomNavigationMenus(updatesRoute, R.string.updates, Icons.Outlined.FilterNone)
}
