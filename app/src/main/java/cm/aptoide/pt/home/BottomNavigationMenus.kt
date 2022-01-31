package cm.aptoide.pt.home

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector
import cm.aptoide.pt.R

sealed class BottomNavigationMenus(val route: String, @StringRes val resourceId: Int,
                                   val icon: ImageVector) {
  object Games : BottomNavigationMenus("games", R.string.games, Icons.Outlined.SportsEsports)
  object Apps : BottomNavigationMenus("apps", R.string.apps, Icons.Outlined.GridView)
  object AppCoins : BottomNavigationMenus("bonus", R.string.bonus, Icons.Outlined.EuroSymbol)
  object Search : BottomNavigationMenus("search", R.string.search, Icons.Outlined.Search)
  object Updates : BottomNavigationMenus("updates", R.string.updates, Icons.Outlined.FilterNone)
}
