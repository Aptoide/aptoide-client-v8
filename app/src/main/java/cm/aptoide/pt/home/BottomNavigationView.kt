package cm.aptoide.pt.home

import android.annotation.SuppressLint
import androidx.compose.animation.ExperimentalAnimationApi
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
import androidx.navigation.compose.currentBackStackEntryAsState
import cm.aptoide.pt.AptoideToolbar
import cm.aptoide.pt.BuildConfig
import cm.aptoide.pt.analytics.presentation.ThemeListener
import cm.aptoide.pt.appview.appViewScreen
import cm.aptoide.pt.appview.reportAppScreen
import cm.aptoide.pt.aptoide_ui.snackbar.AptoideSnackBar
import cm.aptoide.pt.aptoide_ui.theme.AppTheme
import cm.aptoide.pt.aptoide_ui.theme.AptoideTheme
import cm.aptoide.pt.aptoide_ui.urlViewScreen
import cm.aptoide.pt.editorial.editorialScreen
import cm.aptoide.pt.installer.presentation.UserActionDialog
import cm.aptoide.pt.profile.editProfileScreen
import cm.aptoide.pt.profile.profileScreen
import cm.aptoide.pt.search.presentation.searchScreen
import cm.aptoide.pt.settings.presentation.themePreferences
import cm.aptoide.pt.settings.sendFeedbackScreen
import cm.aptoide.pt.settings.settingsScreen
import cm.aptoide.pt.updates.updatesScreen
import com.google.accompanist.navigation.animation.AnimatedNavHost
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MainView(navController: NavHostController) {
  val isDarkTheme = themePreferences(key = "BottomNavigationDarkTheme").first
  val snackBarHostState = remember { SnackbarHostState() }
  val coroutineScope = rememberCoroutineScope()

  ThemeListener {
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
          AptoideToolbar(navController)
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

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun NavigationGraph(
  navController: NavHostController,
  showSnack: (String) -> Unit,
) {
  AnimatedNavHost(
    navController = navController,
    startDestination = gamesRoute
  ) {
    gamesScreen(navigate = navController::navigate)

    appsScreen(navigate = navController::navigate)

    bonusScreen(navigate = navController::navigate)

    searchScreen(navigate = navController::navigate)

    updatesScreen()

    appViewScreen(
      navigateBack = navController::popBackStack,
      navigate = navController::navigate,
    )

    reportAppScreen()

    editorialScreen(
      navigateBack = navController::popBackStack,
    )

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
      showSnack = showSnack,
      versionName = BuildConfig.VERSION_NAME
    )

    sendFeedbackScreen(
      navigateBack = navController::popBackStack,
    )

    urlViewScreen(
      navigateBack = navController::popBackStack
    )
  }
}
