package com.aptoide.android.aptoidegames.home

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.window.Popup
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import cm.aptoide.pt.extensions.animatedComposable
import cm.aptoide.pt.extensions.staticComposable
import cm.aptoide.pt.feature_apkfy.presentation.rememberApkfyApp
import com.aptoide.android.aptoidegames.AptoideGamesBottomSheet
import com.aptoide.android.aptoidegames.BottomSheetContent
import com.aptoide.android.aptoidegames.apkfy.presentation.ApkfyHandler
import com.aptoide.android.aptoidegames.apkfy.presentation.RobloxApkfyMultiInstallScreen
import com.aptoide.android.aptoidegames.apkfy.presentation.apkfyScreen
import com.aptoide.android.aptoidegames.apkfy.presentation.detailedApkfyRoute
import com.aptoide.android.aptoidegames.apkfy.presentation.detailedApkfyScreen
import com.aptoide.android.aptoidegames.apkfy.presentation.robloxApkfyRoute
import com.aptoide.android.aptoidegames.apkfy.presentation.robloxApkfyScreen
import com.aptoide.android.aptoidegames.appview.appViewScreen
import com.aptoide.android.aptoidegames.appview.permissions.appPermissionsScreen
import com.aptoide.android.aptoidegames.bottom_bar.AppGamesBottomBar
import com.aptoide.android.aptoidegames.categories.presentation.allCategoriesScreen
import com.aptoide.android.aptoidegames.categories.presentation.categoryDetailScreen
import com.aptoide.android.aptoidegames.editorial.editorialScreen
import com.aptoide.android.aptoidegames.editorial.seeMoreEditorialScreen
import com.aptoide.android.aptoidegames.feature_apps.presentation.seeAllMyGamesScreen
import com.aptoide.android.aptoidegames.feature_apps.presentation.seeMoreBonusScreen
import com.aptoide.android.aptoidegames.feature_apps.presentation.seeMoreScreen
import com.aptoide.android.aptoidegames.feature_rtb.presentation.rtbSeeMoreScreen
import com.aptoide.android.aptoidegames.gamegenie.presentation.gameGenieScreen
import com.aptoide.android.aptoidegames.gamegenie.presentation.gameGenieSearchScreen
import com.aptoide.android.aptoidegames.gamegenie.presentation.genieRoute
import com.aptoide.android.aptoidegames.gamegenie.presentation.genieSearchRoute
import com.aptoide.android.aptoidegames.gamesfeed.presentation.gamesFeedScreen
import com.aptoide.android.aptoidegames.installer.UserActionDialog
import com.aptoide.android.aptoidegames.launch.rememberIsFirstLaunch
import com.aptoide.android.aptoidegames.notifications.NotificationsPermissionRequester
import com.aptoide.android.aptoidegames.notifications.presentation.rememberNotificationsAnalytics
import com.aptoide.android.aptoidegames.permissions.notifications.NotificationsPermissionViewModel
import com.aptoide.android.aptoidegames.promo_codes.PromoCodeBottomSheet
import com.aptoide.android.aptoidegames.promo_codes.rememberPromoCodeApp
import com.aptoide.android.aptoidegames.promotions.presentation.PromotionDialog
import com.aptoide.android.aptoidegames.search.presentation.searchScreen
import com.aptoide.android.aptoidegames.settings.settingsScreen
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import com.aptoide.android.aptoidegames.toolbar.AppGamesToolBar
import com.aptoide.android.aptoidegames.updates.presentation.updatesScreen
import kotlinx.coroutines.delay
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

  var showTopBar by remember { mutableStateOf(true) }
  val (promoCodeApp, clearPromoCode) = rememberPromoCodeApp()

  val currentRoute =
    navController.currentBackStackEntryFlow.collectAsState(initial = navController.currentBackStackEntry)

  LaunchedEffect(currentRoute.value?.destination?.route) {
    val currentRoute = currentRoute.value?.destination?.route
    showTopBar = if (currentRoute != null) {
      !currentRoute.contains(genieRoute) &&
        !currentRoute.contains(detailedApkfyRoute) &&
        !currentRoute.contains(
          genieSearchRoute
        ) &&
        !currentRoute.contains(robloxApkfyRoute)
    } else {
      true
    }
  }

  //Forced theme do be dark to always apply dark background, for now.
  AptoideTheme(darkTheme = true) {
    AptoideGamesBottomSheet(
      navigate = navController::navigateTo
    ) { showBottomSheet ->
      Scaffold(
        snackbarHost = {
          SnackbarHost(hostState = snackBarHostState) {
            Popup {
              Snackbar(
                snackbarData = it,
                modifier = Modifier
                  .focusable(false)
                  .clearAndSetSemantics {},
              )
            }
          }
        },
        bottomBar = {
          AppGamesBottomBar(navController = navController)
        },
        topBar = {
          if (showTopBar) {
            AppGamesToolBar(
              navigate = {
                if (currentRoute.value?.destination?.route != it) {
                  navController.navigateTo(it)
                }
              },
              goBackHome
            )
          }
        }
      ) { padding ->

        NotificationsPermissionWrapper(
          showDialog = showNotificationsRationaleDialog,
          onDismiss = { notificationsPermissionViewModel.dismissDialog() },
          onPermissionResult = {}
        )

        PromotionDialog(navigate = navController::navigateTo)

        Box(modifier = Modifier.padding(padding)) {
          NavigationGraph(
            navController,
            showSnack = {
              coroutineScope.launch {
                snackBarHostState.showSnackbar(message = it)
              }
            },
            showBottomSheet = showBottomSheet
          )
        }
        ApkfyHandler(navigate = navController::navigateTo)

        LaunchedEffect(promoCodeApp) {
          if (promoCodeApp != null) {
            showBottomSheet(
              PromoCodeBottomSheet(
                promoCode = promoCodeApp,
                showSnack = {
                  coroutineScope.launch {
                    snackBarHostState.showSnackbar(message = it)
                  }
                }
              )
            )
            clearPromoCode()
          }
        }
      }
    }
    UserActionDialog()
  }
}

@SuppressLint("InlinedApi")
@Composable
private fun NotificationsPermissionWrapper(
  showDialog: Boolean,
  onDismiss: () -> Unit,
  onPermissionResult: (Boolean) -> Unit
) {
  val notificationsAnalytics = rememberNotificationsAnalytics()

  val isFirstLaunch = rememberIsFirstLaunch()

  var delayComplete by remember { mutableStateOf(false) }
  var apkfyApp by remember { mutableStateOf<Any?>(null) }

  LaunchedEffect(Unit) {
    delay(5000)
    delayComplete = true
  }

  if (delayComplete) {
    apkfyApp = rememberApkfyApp()
  }

  val isApkfy = apkfyApp != null

  if (isFirstLaunch && !isApkfy && delayComplete) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
      ) { isGranted ->
        onPermissionResult(isGranted)
        if (isGranted) {
          notificationsAnalytics.sendNotificationOptIn()
          notificationsAnalytics.sendExperimentNotificationsAllowed()
        } else {
          notificationsAnalytics.sendNotificationOptOut()
        }
      }

      LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
      }
    }
  } else {
    NotificationsPermissionRequester(
      showDialog = showDialog,
      onDismiss = onDismiss,
      onPermissionResult = onPermissionResult
    )
  }
}

private fun NavBackStackEntry.lifecycleIsResumed() =
  this.lifecycle.currentState == Lifecycle.State.RESUMED

private fun String.getRouteScreenName() = this.substringBefore("/")

fun NavHostController.navigateTo(route: String) {
  currentBackStackEntry?.let {
    if (!it.lifecycleIsResumed()
      && currentDestination?.route?.getRouteScreenName() == route.getRouteScreenName()
    ) { //Avoids duplicate navigation events to the same screen
      return
    } else {
      navigate(route)
    }
  } ?: navigate(route) //Still navigates as a safe measure in case the currentBackStackEntry is null
}

@Composable
private fun NavigationGraph(
  navController: NavHostController,
  showSnack: (String) -> Unit,
  showBottomSheet: (BottomSheetContent?) -> Unit,
) {
  NavHost(
    navController = navController,
    startDestination = gamesRoute
  ) {
    staticComposable(
      navigate = navController::navigateTo,
      goBack = navController::navigateUp,
      screenData = gamesScreen()
    )

    animatedComposable(
      navigate = navController::navigateTo,
      goBack = navController::navigateUp,
      screenData = settingsScreen(showSnack = showSnack)
    )

    animatedComposable(
      navigate = navController::navigateTo,
      goBack = navController::navigateUp,
      screenData = appViewScreen()
    )

    staticComposable(
      navigate = navController::navigateTo,
      goBack = navController::navigateUp,
      screenData = searchScreen()
    )

    animatedComposable(
      navigate = navController::navigateTo,
      goBack = navController::navigateUp,
      screenData = editorialScreen()
    )

    animatedComposable(
      navigate = navController::navigateTo,
      goBack = navController::navigateUp,
      screenData = seeMoreScreen()
    )

    animatedComposable(
      navigate = navController::navigateTo,
      goBack = navController::navigateUp,
      screenData = seeMoreBonusScreen()
    )

    animatedComposable(
      navigate = navController::navigateTo,
      goBack = navController::navigateUp,
      screenData = rtbSeeMoreScreen()
    )

    animatedComposable(
      navigate = navController::navigateTo,
      goBack = navController::navigateUp,
      screenData = seeAllMyGamesScreen()
    )

    staticComposable(
      navigate = navController::navigateTo,
      goBack = navController::navigateUp,
      screenData = allCategoriesScreen()
    )

    animatedComposable(
      navigate = navController::navigateTo,
      goBack = navController::navigateUp,
      screenData = seeMoreEditorialScreen()
    )

    animatedComposable(
      navigate = navController::navigateTo,
      goBack = navController::navigateUp,
      screenData = categoryDetailScreen()
    )

    animatedComposable(
      navigate = navController::navigateTo,
      goBack = navController::navigateUp,
      screenData = appPermissionsScreen()
    )

    staticComposable(
      navigate = navController::navigateTo,
      goBack = navController::navigateUp,
      screenData = updatesScreen()
    )

    staticComposable(
      navigate = navController::navigateTo,
      goBack = navController::navigateUp,
      screenData = gameGenieScreen(showBottomSheet = showBottomSheet)
    )

    staticComposable(
      navigate = navController::navigateTo,
      goBack = navController::navigateUp,
      screenData = gameGenieSearchScreen()
    )

    animatedComposable(
      navigate = navController::navigateTo,
      goBack = navController::navigateUp,
      screenData = apkfyScreen()
    )

    animatedComposable(
      navigate = navController::navigateTo,
      goBack = navController::navigateUp,
      screenData = detailedApkfyScreen()
    )

    animatedComposable(
      navigate = navController::navigateTo,
      goBack = navController::navigateUp,
      screenData = robloxApkfyScreen()
    )

    animatedComposable(
      navigate = navController::navigateTo,
      goBack = navController::navigateUp,
      screenData = RobloxApkfyMultiInstallScreen()
    )

    animatedComposable(
      navigate = navController::navigateTo,
      goBack = navController::navigateUp,
      screenData = gamesFeedScreen()
    )
  }
}
