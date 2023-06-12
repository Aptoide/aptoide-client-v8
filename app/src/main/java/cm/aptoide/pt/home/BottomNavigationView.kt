package cm.aptoide.pt.home

import android.annotation.SuppressLint
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import cm.aptoide.pt.analytics.presentation.ThemeListener
import cm.aptoide.pt.aptoide_ui.snackbar.AptoideSnackBar
import cm.aptoide.pt.aptoide_ui.theme.AppTheme
import cm.aptoide.pt.aptoide_ui.theme.AptoideTheme
import cm.aptoide.pt.aptoide_ui.toolbar.AptoideActionBar
import cm.aptoide.pt.aptoide_ui.urlViewScreen
import cm.aptoide.pt.feature_home.presentation.BundlesScreen
import cm.aptoide.pt.feature_home.presentation.ScreenType
import cm.aptoide.pt.feature_search.presentation.search.SearchScreen
import cm.aptoide.pt.feature_updates.presentation.UpdatesScreen
import cm.aptoide.pt.profile.presentation.ProfileButton
import cm.aptoide.pt.profile.presentation.editProfileScreen
import cm.aptoide.pt.profile.presentation.profileRoute
import cm.aptoide.pt.profile.presentation.profileScreen
import cm.aptoide.pt.settings.presentation.sendFeedbackScreen
import cm.aptoide.pt.settings.presentation.settingsScreen
import cm.aptoide.pt.settings.presentation.themePreferences
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MainView(shouldShowBottomNavigation: Boolean) {
  val navController = rememberNavController()
  val isDarkTheme = themePreferences(key = "BottomNavigationDarkTheme").first
  val snackBarHostState = remember { SnackbarHostState() }
  val coroutineScope = rememberCoroutineScope()

  ThemeListener {
    AptoideTheme(darkTheme = isDarkTheme ?: isSystemInDarkTheme()) {
      if (shouldShowBottomNavigation) {
        Scaffold(
          snackbarHost = {
            SnackbarHost(
              hostState = snackBarHostState,
              snackbar = { AptoideSnackBar(it) }
            )
          },
          bottomBar = {
            BottomNavigation(navController)
          }
        ) {
          Box(modifier = Modifier.padding(it)) {
            NavigationGraph(
              navController,
              showSnack = {
                coroutineScope.launch {
                  snackBarHostState.showSnackbar(message = it)
                }
              }
            )
          }
        }
      } else {
        Scaffold {
          BundlesScreen(
            viewModel = hiltViewModel(),
            type = ScreenType.GAMES,
          ) {
            AptoideActionBar {
              ProfileButton {
                navController.navigate(profileRoute)
              }
            }
          }
        }
      }
    }
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
private fun NavigationGraph(
  navController: NavHostController,
  showSnack: (String) -> Unit,
) {
  NavHost(
    navController = navController,
    startDestination = BottomNavigationMenus.Games.route
  ) {
    composable(BottomNavigationMenus.Games.route) {
      BundlesScreen(
        viewModel = hiltViewModel(),
        type = ScreenType.GAMES,
      ) {
        AptoideActionBar {
          ProfileButton {
            navController.navigate(profileRoute)
          }
        }
      }
    }
    composable(BottomNavigationMenus.Apps.route) {
      BundlesScreen(
        viewModel = hiltViewModel(),
        type = ScreenType.APPS,
      ) {
        AptoideActionBar {
          ProfileButton {
            navController.navigate(profileRoute)
          }
        }
      }
    }
    composable(BottomNavigationMenus.AppCoins.route) {
      BundlesScreen(
        viewModel = hiltViewModel(),
        type = ScreenType.BONUS,
      ) {
        AptoideActionBar {
          ProfileButton {
            navController.navigate(profileRoute)
          }
        }
      }
    }
    composable(BottomNavigationMenus.Search.route) {
      SearchScreen()
    }
    composable(BottomNavigationMenus.Updates.route) {
      UpdatesScreen()
    }

    profileScreen(
      navigate = navController::navigate,
      navigateBack = navController::popBackStack,
    )

    editProfileScreen(
      navigateBack = navController::popBackStack,
      showSnack = showSnack
    )

    settingsScreen(
      navigate = navController::navigate,
      navigateBack = navController::popBackStack,
      showSnack = showSnack
    )

    sendFeedbackScreen(
      navigateBack = navController::popBackStack,
      showSnack = showSnack
    )

    urlViewScreen(
      navigateBack = navController::popBackStack
    )
  }
}
