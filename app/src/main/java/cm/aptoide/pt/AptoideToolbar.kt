package cm.aptoide.pt

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import cm.aptoide.pt.home.appsRoute
import cm.aptoide.pt.home.bonusRoute
import cm.aptoide.pt.home.gamesRoute
import cm.aptoide.pt.profile.ProfileButton
import cm.aptoide.pt.profile.profileRoute
import cm.aptoide.pt.toolbar.AptoideActionBar

@Composable
fun AptoideToolbar(
  navController: NavHostController,
) {
  val currentBackStackEntry by navController.currentBackStackEntryAsState()
  val currentRoute by remember { derivedStateOf { currentBackStackEntry?.destination?.route } }

  if (shouldShowToolbar(currentRoute)) {
    AptoideActionBar {
      ProfileButton {
        navController.navigate(profileRoute)
      }
    }
  }
}

private fun shouldShowToolbar(currentRoute: String?): Boolean {
  return when (currentRoute) {
    gamesRoute, appsRoute, bonusRoute -> true
    else -> false
  }
}
