package cm.aptoide.pt.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import cm.aptoide.pt.BuildConfig
import cm.aptoide.pt.R
import cm.aptoide.pt.feature_apps.presentation.BundlesScreen
import cm.aptoide.pt.feature_apps.presentation.ScreenType
import cm.aptoide.pt.feature_search.presentation.search.SearchScreen
import cm.aptoide.pt.feature_settings.presentation.SettingsScreen
import cm.aptoide.pt.feature_updates.presentation.UpdatesScreen
import cm.aptoide.pt.theme.AppTheme
import cm.aptoide.pt.theme.AptoideTheme

@Composable
fun MainView(dataStore: DataStore<Preferences>, shouldShowBottomNavigation: Boolean) {
  val navController = rememberNavController()

  if (shouldShowBottomNavigation) {
    Scaffold(
      topBar = {
        AptoideActionBar(navController)
      },
      bottomBar = {
        BottomNavigation(navController)
      }
    ) {
      NavigationGraph(navController, dataStore, it)
    }
  } else {
    Scaffold(
      topBar = {
        AptoideActionBar(navController)
      }
    ) {
      BundlesScreen(viewModel = hiltViewModel(), type = ScreenType.GAMES)
    }
  }
}

@Composable
fun AptoideActionBar(navController: NavHostController) {
  AptoideTheme {
    TopAppBar(modifier = Modifier.padding(start = 16.dp, end = 16.dp),
      backgroundColor = AppTheme.colors.background, elevation = Dp(0f)
    ) {
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        AptoideIcon()
        Text(modifier = Modifier.padding(start = 8.dp), text = BuildConfig.MARKET_NAME)
        Icon(imageVector = Icons.Outlined.Settings,
          contentDescription = null,
          Modifier.clickable {
            navController.navigate("settings")
          })
      }
    }
  }
}

@Composable
fun AptoideIcon() {
  Image(
    painter = painterResource(id = R.drawable.ic_aptoide_icon),
    contentDescription = "Aptoide Icon"
  )
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
private fun NavigationGraph(
  navController: NavHostController,
  dataStore: DataStore<Preferences>,
  paddingValues: PaddingValues,
) {
  NavHost(
    navController = navController,
    startDestination = BottomNavigationMenus.Games.route,
    modifier = Modifier.padding(paddingValues)
  ) {
    composable(BottomNavigationMenus.Games.route) {
      BundlesScreen(viewModel = hiltViewModel(), type = ScreenType.GAMES)
    }
    composable(BottomNavigationMenus.Apps.route) {
      BundlesScreen(viewModel = hiltViewModel(), type = ScreenType.APPS)
    }
    composable(BottomNavigationMenus.AppCoins.route) {
      BundlesScreen(viewModel = hiltViewModel(), type = ScreenType.BONUS)
    }
    composable(BottomNavigationMenus.Search.route) {
      SearchScreen()
    }
    composable(BottomNavigationMenus.Updates.route) {
      UpdatesScreen()
    }

    composable("settings") {
      SettingsScreen(dataStore)
    }
  }
}