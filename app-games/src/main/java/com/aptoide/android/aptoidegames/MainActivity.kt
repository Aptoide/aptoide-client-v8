package com.aptoide.android.aptoidegames

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.LaunchedEffect
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
import com.aptoide.android.aptoidegames.notifications.toFirebaseNotificationAnalyticsInfo
import com.aptoide.android.aptoidegames.promo_codes.PromoCodeApp
import com.aptoide.android.aptoidegames.promo_codes.PromoCodeRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
      generalAnalytics.sendOpenAppEvent(
        appOpenSource = intent.appOpenSource,
        isFirstLaunch = isFirstLaunch,
        networkType = getNetworkType()
      )
      if (isFirstLaunch) {
        appLaunchPreferencesManager.setIsNotFirstLaunch()
        biAnalytics.sendFirstLaunchEvent()
      } else {
        generalAnalytics.sendEngagedUserEvent()
      }
    }
  }

  private fun handleNotificationIntent(intent: Intent?) {
    intent.takeIf { it.isAhab }?.agDeepLink.takeIf { it?.scheme == "promocode" }?.run {
      promoCodeRepository.setPromoCodeApp(PromoCodeApp(host!!, path!!))
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
