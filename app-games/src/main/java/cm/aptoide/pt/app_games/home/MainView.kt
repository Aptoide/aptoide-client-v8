package cm.aptoide.pt.app_games.home

import android.annotation.SuppressLint
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import cm.aptoide.pt.app_games.notifications.NotificationsPermissionRequester
import androidx.navigation.compose.currentBackStackEntryAsState
import cm.aptoide.pt.app_games.appview.appViewScreen
import cm.aptoide.pt.app_games.bottom_bar.AppGamesBottomBar
import cm.aptoide.pt.app_games.installer.UserActionDialog
import cm.aptoide.pt.app_games.notifications.NotificationsPermissionViewModel
import cm.aptoide.pt.app_games.search.presentation.searchScreen
import cm.aptoide.pt.app_games.settings.settingsScreen
import cm.aptoide.pt.app_games.theme.AptoideSnackBar
import cm.aptoide.pt.app_games.theme.AptoideTheme
import cm.aptoide.pt.app_games.toolbar.AppGamesToolBar
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MainView(navController: NavHostController) {
  val themeViewModel = hiltViewModel<AppThemeViewModel>()
  val isDarkTheme by themeViewModel.uiState.collectAsState()
  val notificationsPermissionViewModel = hiltViewModel<NotificationsPermissionViewModel>()
  val showNotificationsRationaleDialog by notificationsPermissionViewModel.uiState.collectAsState()
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
        AppGamesBottomBar(navController = navController)
      },
      topBar = {
        AppGamesToolBar(navigate = navController::navigate, goBackHome)
      }
    ) { padding ->
      if (showNotificationsRationaleDialog) {
        NotificationsPermissionRequester(
          onDismiss = notificationsPermissionViewModel::dismissDialog
        )
      }

      Box(modifier = Modifier.padding(padding)) {
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
private fun NavigationGraph(
  navController: NavHostController,
  showSnack: (String) -> Unit,
) {
  NavHost(
    navController = navController,
    startDestination = gamesRoute
  ) {
    gamesScreen(
      navigate = navController::navigate
    )

    settingsScreen(
      navigateBack = navController::popBackStack,
    )

    appViewScreen(
      navigateBack = navController::popBackStack,
    )

    searchScreen(
      navigate = navController::navigate,
    )

  }
}
