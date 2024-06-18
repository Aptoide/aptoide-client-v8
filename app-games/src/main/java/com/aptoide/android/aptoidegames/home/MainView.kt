package com.aptoide.android.aptoidegames.home

import android.annotation.SuppressLint
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
import cm.aptoide.pt.extensions.animatedComposable
import cm.aptoide.pt.extensions.staticComposable
import com.aptoide.android.aptoidegames.appview.appViewScreen
import com.aptoide.android.aptoidegames.appview.permissions.appPermissionsScreen
import com.aptoide.android.aptoidegames.bottom_bar.AppGamesBottomBar
import com.aptoide.android.aptoidegames.categories.presentation.allCategoriesScreen
import com.aptoide.android.aptoidegames.categories.presentation.categoryDetailScreen
import com.aptoide.android.aptoidegames.design_system.AptoideSnackBar
import com.aptoide.android.aptoidegames.editorial.editorialScreen
import com.aptoide.android.aptoidegames.feature_apps.presentation.seeAllMyGamesScreen
import com.aptoide.android.aptoidegames.feature_apps.presentation.seeMoreScreen
import com.aptoide.android.aptoidegames.installer.UserActionDialog
import com.aptoide.android.aptoidegames.notifications.NotificationsPermissionRequester
import com.aptoide.android.aptoidegames.notifications.NotificationsPermissionViewModel
import com.aptoide.android.aptoidegames.search.presentation.searchScreen
import com.aptoide.android.aptoidegames.settings.settingsScreen
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import com.aptoide.android.aptoidegames.toolbar.AppGamesToolBar
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MainView(navController: NavHostController) {
  //val themeViewModel = hiltViewModel<AppThemeViewModel>()
  //val isDarkTheme by themeViewModel.uiState.collectAsState()
  val notificationsPermissionViewModel = hiltViewModel<NotificationsPermissionViewModel>()
  val showNotificationsRationaleDialog by notificationsPermissionViewModel.uiState.collectAsState()
  val snackBarHostState = remember { SnackbarHostState() }
  val coroutineScope = rememberCoroutineScope()
  val goBackHome: () -> Unit =
    { navController.popBackStack(navController.graph.startDestinationId, false) }
  //Forced theme do be dark to always apply dark background, for now.
  AptoideTheme(darkTheme = true) {
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
    staticComposable(
      navigate = navController::navigate,
      goBack = navController::popBackStack,
      screenData = gamesScreen()
    )

    animatedComposable(
      navigate = navController::navigate,
      goBack = navController::popBackStack,
      screenData = settingsScreen(showSnack = showSnack)
    )

    animatedComposable(
      navigate = navController::navigate,
      goBack = navController::popBackStack,
      screenData = appViewScreen()
    )

    animatedComposable(
      navigate = navController::navigate,
      goBack = navController::popBackStack,
      screenData = searchScreen()
    )

    animatedComposable(
      navigate = navController::navigate,
      goBack = navController::popBackStack,
      screenData = editorialScreen()
    )

    animatedComposable(
      navigate = navController::navigate,
      goBack = navController::navigateUp,
      screenData = seeMoreScreen()
    )

    animatedComposable(
      navigate = navController::navigate,
      goBack = navController::navigateUp,
      screenData = seeAllMyGamesScreen()
    )

    staticComposable(
      navigate = navController::navigate,
      goBack = navController::navigateUp,
      screenData = allCategoriesScreen()
    )

    animatedComposable(
      navigate = navController::navigate,
      goBack = navController::navigateUp,
      screenData = categoryDetailScreen()
    )

    animatedComposable(
      navigate = navController::navigate,
      goBack = navController::navigateUp,
      screenData = appPermissionsScreen()
    )
  }
}
