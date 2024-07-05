package com.aptoide.android.aptoidegames

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import cm.aptoide.pt.install_manager.InstallManager
import com.aptoide.android.aptoidegames.analytics.GenericAnalytics
import com.aptoide.android.aptoidegames.analytics.getNetworkType
import com.aptoide.android.aptoidegames.home.MainView
import com.aptoide.android.aptoidegames.installer.notifications.InstallerNotificationsBuilder
import com.aptoide.android.aptoidegames.launch.AppLaunchPreferencesManager
import com.aptoide.android.aptoidegames.network.repository.NetworkPreferencesRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

  @Inject
  lateinit var genericAnalytics: GenericAnalytics

  @Inject
  lateinit var appLaunchPreferencesManager: AppLaunchPreferencesManager

  @Inject
  lateinit var installManager: InstallManager

  @Inject
  lateinit var networkPreferencesRepository: NetworkPreferencesRepository

  private var navController: NavHostController? = null

  private val coroutinesScope: CoroutineScope = CoroutineScope(Job() + Dispatchers.IO)

  val requestPermissionLauncher =
    registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
      coroutinesScope.launch {
        if (isGranted) {
          genericAnalytics.sendNotificationOptIn()
        } else {
          genericAnalytics.sendNotificationOptOut()
        }
      }
    }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    sendAGStartAnalytics()
    setContent {
      val navController = rememberNavController()
        .also { this.navController = it }

      MainView(navController)

      LaunchedEffect(key1 = navController) {
        handleNotificationIntent(intent = intent)
      }
    }
  }

  private fun sendAGStartAnalytics() {
    CoroutineScope(Dispatchers.IO).launch {
      val isFirstLaunch = appLaunchPreferencesManager.isFirstLaunch()
      genericAnalytics.sendOpenAppEvent(
        appOpenSource = intent.appOpenSource,
        isFirstLaunch = isFirstLaunch,
        networkType = getNetworkType()
      )
      if (isFirstLaunch) {
        appLaunchPreferencesManager.setIsNotFirstLaunch()
      } else {
        genericAnalytics.sendEngagedUserEvent()
      }
    }
  }

  private fun handleNotificationIntent(intent: Intent?) {
    CoroutineScope(Dispatchers.IO).launch {
      intent?.getStringExtra(InstallerNotificationsBuilder.ALLOW_METERED_DOWNLOAD_FOR_PACKAGE)
        ?.let(installManager::getApp)
        ?.task
        ?.also {
          genericAnalytics.sendDownloadNowClicked(
            downloadOnlyOverWifi = networkPreferencesRepository
              .shouldDownloadOnlyOverWifi()
              .first(),
            promptType = "notification",
            packageName = it.packageName,
            appSize = it.installPackageInfo.filesSize
          )
        }
        ?.allowDownloadOnMetered()
    }
    intent.agDeepLink?.let {
      navController?.navigate(it)
    }
  }

  override fun onNewIntent(intent: Intent?) {
    super.onNewIntent(intent)
    navController?.handleDeepLink(intent)
    handleNotificationIntent(intent)
  }
}
