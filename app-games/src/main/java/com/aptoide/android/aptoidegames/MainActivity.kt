package com.aptoide.android.aptoidegames

import android.Manifest
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import cm.aptoide.pt.install_manager.InstallManager
import com.aptoide.android.aptoidegames.analytics.BIAnalytics
import com.aptoide.android.aptoidegames.analytics.GeneralAnalytics
import com.aptoide.android.aptoidegames.analytics.presentation.withPrevScreen
import com.aptoide.android.aptoidegames.firebase.FirebaseConstants
import com.aptoide.android.aptoidegames.home.MainView
import com.aptoide.android.aptoidegames.installer.analytics.InstallAnalytics
import com.aptoide.android.aptoidegames.installer.analytics.getNetworkType
import com.aptoide.android.aptoidegames.installer.notifications.InstallerNotificationsBuilder
import com.aptoide.android.aptoidegames.launch.AppLaunchPreferencesManager
import com.aptoide.android.aptoidegames.network.repository.NetworkPreferencesRepository
import com.aptoide.android.aptoidegames.notifications.analytics.FirebaseNotificationAnalytics
import com.aptoide.android.aptoidegames.notifications.analytics.NotificationsAnalytics
import com.aptoide.android.aptoidegames.notifications.toFirebaseNotificationAnalyticsInfo
import com.aptoide.android.aptoidegames.promo_codes.PromoCode
import com.aptoide.android.aptoidegames.promo_codes.PromoCodeRepository
import com.aptoide.android.aptoidegames.usage_stats.UsageStatsPermissionViewModel
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
  lateinit var generalAnalytics: GeneralAnalytics

  @Inject
  lateinit var installAnalytics: InstallAnalytics

  @Inject
  lateinit var notificationsAnalytics: NotificationsAnalytics

  @Inject
  lateinit var biAnalytics: BIAnalytics

  @Inject
  lateinit var appLaunchPreferencesManager: AppLaunchPreferencesManager

  @Inject
  lateinit var installManager: InstallManager

  @Inject
  lateinit var networkPreferencesRepository: NetworkPreferencesRepository

  @Inject
  lateinit var promoCodeRepository: PromoCodeRepository

  @Inject
  lateinit var firebaseNotificationAnalytics: FirebaseNotificationAnalytics

  private var navController: NavHostController? = null

  private val coroutinesScope: CoroutineScope = CoroutineScope(Job() + Dispatchers.IO)

  private var notificationsPermissionLauncher: ActivityResultLauncher<String>? = null

  @RequiresApi(Build.VERSION_CODES.Q)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    intent.addSourceContext()

    notificationsPermissionLauncher =
      registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        coroutinesScope.launch {
          if (isGranted) {
            notificationsAnalytics.sendNotificationOptIn()
          } else {
            notificationsAnalytics.sendNotificationOptOut()
          }
        }
      }

    handleStartup()
    setContent {
      val navController = rememberNavController()
        .also { this.navController = it }

      val statsPermissions = hiltViewModel<UsageStatsPermissionViewModel>()
      val context = LocalContext.current

      MainView(navController)

      LaunchedEffect(key1 = navController) {
        handleNotificationIntent(intent = intent)
      }

      LaunchedEffect(Unit) {
        val accepted = statsPermissions.requestUsageStatsPermission()

        if (accepted) {
          println("AAAAA accepted")
          val usageStatsManager = context.getSystemService(USAGE_STATS_SERVICE) as UsageStatsManager

          val startTime = System.currentTimeMillis() - 1000 * 60 * 60 * 24
          val endTime = System.currentTimeMillis()

          val usageStatsList: MutableList<UsageStats> = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            startTime,
            endTime
          )

          usageStatsList.forEach { usageStats ->
            usageStats.lastTimeForegroundServiceUsed
            if (usageStats.packageName == "com.roblox.client")
              println(
                "Usage Stats:\n" +
                  "├─ Package: ${usageStats.packageName}\n" +
                  "├─ Last Time Used: ${usageStats.lastTimeUsed} ms epoch\n" +
                  "├─ Last Time Visible: ${usageStats.lastTimeVisible} ms epoch\n" +
                  "├─ First Time Stamp: ${usageStats.firstTimeStamp} ms epoch\n" +
                  "├─ Last Time Stamp: ${usageStats.lastTimeStamp} ms epoch\n" +
                  "├─ Total Time in Foreground: ${usageStats.totalTimeInForeground} ms\n" +
                  "├─ Total Time Visible: ${usageStats.totalTimeVisible} ms\n" +
                  "├─ Last Time Foreground Service Used: ${usageStats.lastTimeForegroundServiceUsed} ms epoch\n" +
                  "└─ Total Time Foreground Service Used: ${usageStats.totalTimeForegroundServiceUsed} ms\n"
              )
          }

          val usageStatsAggregated = usageStatsManager.queryAndAggregateUsageStats(
            System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 7,
            endTime
          )

          usageStatsAggregated.forEach {
            if (it.key == "com.roblox.client") {
              val usageStats = it.value
              println(
                "Usage Stats Aggregated:\n" +
                  "├─ Package: ${usageStats.packageName}\n" +
                  "├─ Last Time Used: ${usageStats.lastTimeUsed} ms epoch\n" +
                  "├─ Last Time Visible: ${usageStats.lastTimeVisible} ms epoch\n" +
                  "├─ First Time Stamp: ${usageStats.firstTimeStamp} ms epoch\n" +
                  "├─ Last Time Stamp: ${usageStats.lastTimeStamp} ms epoch\n" +
                  "├─ Total Time in Foreground: ${usageStats.totalTimeInForeground} ms\n" +
                  "├─ Total Time Visible: ${usageStats.totalTimeVisible} ms\n" +
                  "├─ Last Time Foreground Service Used: ${usageStats.lastTimeForegroundServiceUsed} ms epoch\n" +
                  "└─ Total Time Foreground Service Used: ${usageStats.totalTimeForegroundServiceUsed} ms\n"
              )
            }
          }
        }
      }

    }
  }

  private fun handleStartup() {
    CoroutineScope(Dispatchers.Main).launch {
      val isFirstLaunch = appLaunchPreferencesManager.isFirstLaunch()
      sendAGStartAnalytics(isFirstLaunch)

      if (isFirstLaunch) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
          runCatching {
            notificationsPermissionLauncher?.launch(Manifest.permission.POST_NOTIFICATIONS)
          }
        }
        appLaunchPreferencesManager.setIsNotFirstLaunch()
      }
    }
  }

  private fun sendAGStartAnalytics(isFirstLaunch: Boolean) {
    generalAnalytics.sendOpenAppEvent(
      appOpenSource = intent.appOpenSource,
      isFirstLaunch = isFirstLaunch,
      networkType = getNetworkType()
    )
    if (isFirstLaunch) {
      biAnalytics.sendFirstLaunchEvent()
    } else {
      generalAnalytics.sendEngagedUserEvent()
    }
  }

  private fun handleNotificationIntent(intent: Intent?) {
    intent.takeIf { it.isAhab }?.agDeepLink.takeIf { it?.scheme == "promocode" }?.run {
      promoCodeRepository.setPromoCode(
        PromoCode(
          packageName = host!!,
          code = path!!,
          value = getQueryParameter("value")?.toIntOrNull()?.takeIf { it in 1..100 }
        )
      )
    }

    intent.externalUrl?.takeIf { it.scheme in listOf("http", "https") }
      ?.let { uri ->
        if (uri.toString().isNotEmpty()) {
          if (intent.shouldOpenWebView) {
            UrlActivity.open(this, uri.toString())
          } else {
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
          }
        }
      }

    CoroutineScope(Dispatchers.IO).launch {
      intent?.getStringExtra(InstallerNotificationsBuilder.ALLOW_METERED_DOWNLOAD_FOR_PACKAGE)
        ?.let(installManager::getApp)
        ?.task
        ?.also {
          installAnalytics.sendDownloadNowClicked(
            packageName = it.packageName,
            appSize = it.installPackageInfo.filesSize,
            promptType = "notification",
            downloadOnlyOverWifi = networkPreferencesRepository
              .shouldDownloadOnlyOverWifi()
              .first()
          )
        }
        ?.allowDownloadOnMetered()
    }
    intent.agDeepLink?.takeIf { it.scheme == "ag" }?.let {
      navController?.navigate(it)
    }

    intent.takeIf { it.isAGNotification }?.let {
      val notificationTag = it.notificationTag
      val notificationPackage = it.notificationPackage

      notificationsAnalytics.sendExperimentNotificationClick(notificationTag!!, notificationPackage)
      notificationsAnalytics.sendNotificationOpened(notificationTag!!, notificationPackage)
    }

    handleFirebaseNotificationAnalytics(intent)
  }

  private fun handleFirebaseNotificationAnalytics(intent: Intent?) {
    val messageId = intent?.extras?.getString(FirebaseConstants.FIREBASE_MESSAGE_ID)
    val notificationAnalyticsBundle =
      intent?.extras?.getBundle(FirebaseConstants.FIREBASE_ANALYTICS_DATA)

    if (messageId != null && notificationAnalyticsBundle != null) {
      notificationAnalyticsBundle.putString(FirebaseConstants.FIREBASE_MESSAGE_ID, messageId)

      notificationAnalyticsBundle.toFirebaseNotificationAnalyticsInfo()?.let {
        firebaseNotificationAnalytics.sendNotificationOpened(it)
      }
    }
  }

  override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    intent.addSourceContext()
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
