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
import com.aptoide.android.aptoidegames.analytics.BIAnalytics
import com.aptoide.android.aptoidegames.analytics.GenericAnalytics
import com.aptoide.android.aptoidegames.analytics.getNetworkType
import com.aptoide.android.aptoidegames.analytics.presentation.withPrevScreen
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
  lateinit var biAnalytics: BIAnalytics

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
    intent.addSourceContext()

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
    CoroutineScope(Dispatchers.Main).launch {
      val isFirstLaunch = appLaunchPreferencesManager.isFirstLaunch()
      genericAnalytics.sendOpenAppEvent(
        appOpenSource = intent.appOpenSource,
        isFirstLaunch = isFirstLaunch,
        networkType = getNetworkType()
      )
      if (isFirstLaunch) {
        appLaunchPreferencesManager.setIsNotFirstLaunch()
        biAnalytics.sendFirstLaunchEvent()
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
    intent?.addSourceContext()
    navController?.handleDeepLink(intent)
    handleNotificationIntent(intent)
  }

  private fun Intent.addSourceContext() {
    val currentData = data ?: return

    val deeplinkScheme = BuildConfig.DEEP_LINK_SCHEMA.substringBefore("://")

    data = when (currentData.scheme) {
      deeplinkScheme -> currentData.withPrevScreen("deeplink")

      "http",
      "https",
      -> currentData.withPrevScreen("app_link")

      else -> currentData
    }
  }
}
