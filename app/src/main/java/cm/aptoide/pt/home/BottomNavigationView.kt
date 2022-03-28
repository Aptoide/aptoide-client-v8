package cm.aptoide.pt.home

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import cm.aptoide.pt.feature_apps.presentation.AppsScreen
import cm.aptoide.pt.feature_search.presentation.search.SearchScreen
import cm.aptoide.pt.feature_search.presentation.search.SearchViewModel
import cm.aptoide.pt.home.appcoins.BonusScreen
import cm.aptoide.pt.home.games.GamesScreen
import cm.aptoide.pt.home.updates.UpdatesScreen
import cm.aptoide.pt.theme.AppTheme

@Preview
@Composable
fun MainView() {
  val navController = rememberNavController()
  Scaffold(
    bottomBar = {
      BottomNavigation(navController)
    }
  ) {
    NavigationGraph(navController)
  }
}

@Composable
private fun BottomNavigation(navController: NavHostController) {
  val items = listOf(
    BottomNavigationMenus.Games,
    BottomNavigationMenus.Apps,
    BottomNavigationMenus.AppCoins,
    BottomNavigationMenus.Search,
    BottomNavigationMenus.Updates
  )
  CompositionLocalProvider(LocalElevationOverlay provides null) {
    BottomNavigation(backgroundColor = AppTheme.colors.surface) {
      val navBackStackEntry by navController.currentBackStackEntryAsState()
      val currentDestination = navBackStackEntry?.destination
      items.forEach { screen ->
        val selected = currentDestination?.hierarchy?.any { it.route == screen.route }
        BottomNavigationItem(
          icon = { Icon(imageVector = screen.icon, contentDescription = null) },
          selected = selected == true,
          label = {
            Text(
              text = stringResource(id = screen.resourceId),
              color = if (selected == true) AppTheme.colors.primary else AppTheme.colors.unselectedLabelColor
            )
          },
          selectedContentColor = AppTheme.colors.primary,
          unselectedContentColor = AppTheme.colors.unselectedLabelColor,
          alwaysShowLabel = true,
          onClick = {
            navController.navigate(screen.route) {
              // Pop up to the start destination of the graph to
              // avoid building up a large stack of destinations
              // on the back stack as users select items
              popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
              }
              // Avoid multiple copies of the same destination when
              // reselecting the same item
              launchSingleTop = true
              // Restore state when reselecting a previously selected item
              restoreState = true
            }
          })
      }
    }
  }
}

@Composable
private fun NavigationGraph(navController: NavHostController) {
  NavHost(
    navController = navController,
    startDestination = BottomNavigationMenus.Games.route
  ) {
    composable(BottomNavigationMenus.Games.route) {
      GamesScreen()
    }
    composable(BottomNavigationMenus.Apps.route) {
      AppsScreen()
    }
    composable(BottomNavigationMenus.AppCoins.route) {
      BonusScreen()
    }
    composable(BottomNavigationMenus.Search.route) {
      SearchScreen()
    }
    composable(BottomNavigationMenus.Updates.route) {
      UpdatesScreen()
    }
  }
}