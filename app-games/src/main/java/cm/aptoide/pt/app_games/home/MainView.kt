package cm.aptoide.pt.app_games.home

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
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import cm.aptoide.pt.app_games.settings.settingsScreen
import cm.aptoide.pt.app_games.toolbar.AppGamesToolBar
import cm.aptoide.pt.aptoide_ui.snackbar.AptoideSnackBar
import cm.aptoide.pt.aptoide_ui.theme.*
import cm.aptoide.pt.installer.presentation.UserActionDialog
import cm.aptoide.pt.settings.presentation.themePreferences
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MainView(navController: NavHostController) {
  val isDarkTheme = themePreferences(key = "BottomNavigationDarkTheme").first
  val snackBarHostState = remember { SnackbarHostState() }
  val coroutineScope = rememberCoroutineScope()
  val goBackHome: () -> Unit =
    { navController.popBackStack(navController.graph.startDestinationId, false) }

  AptoideTheme(darkTheme = isDarkTheme ?: isSystemInDarkTheme()) {
    Scaffold(
      snackbarHost = {
        SnackbarHost(
          hostState = snackBarHostState,
          snackbar = { AptoideSnackBar(it) }
        )
      },
      bottomBar = {
        BottomNavigation(navController)
      },
      topBar = {
        AppGamesToolBar(navigate = navController::navigate, goBackHome)
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
    UserActionDialog()
  }
}

@Composable
private fun BottomNavigation(navController: NavHostController) {
  val items = listOf(
    BottomBarMenus.Games,
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
    startDestination = gamesRoute
  ) {
    gamesScreen(navigate = navController::navigate)

    settingsScreen(
      navigateBack = navController::popBackStack,
    )

  }
}
