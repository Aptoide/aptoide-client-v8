package com.aptoide.android.aptoidegames.feature_companion_apps_notification

import android.content.Context
import android.os.Build
import cm.aptoide.pt.feature_flags.domain.FeatureFlags
import cm.aptoide.pt.install_manager.InstallManager
import com.aptoide.android.aptoidegames.notifications.analytics.NotificationsAnalytics
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CompanionAppsManager @Inject constructor(
  private val installManager: InstallManager,
  private val featureFlags: FeatureFlags,
  private val notificationsAnalytics: NotificationsAnalytics,
  @ApplicationContext private val context: Context
) {

  suspend fun initialize() {
    val delay = featureFlags.getFlagAsString("roblox_related_apps_delay", "0").toLong()
    Timber.d("delay for companion apps ab test is $delay")
    installManager.appsChanges.filter { it.packageName == "com.roblox.client" }
      .filter {
        it.packageInfo != null && shouldTriggerCompanionNotification(it.updatesOwnerPackageName)
      }.map {
        notificationsAnalytics.sendCompanionAppNotificationOptIn()
        if (delay > 0) {
          CompanionAppsWorker.enqueue(context, "com.roblox.client", delay)
        }
      }.firstOrNull()
  }

  private fun shouldTriggerCompanionNotification(updatesOwner: String?): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      updatesOwner == context.packageName
    } else {
      true
    }
  }
}
