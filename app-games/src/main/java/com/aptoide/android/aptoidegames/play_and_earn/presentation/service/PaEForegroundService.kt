package com.aptoide.android.aptoidegames.play_and_earn.presentation.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import cm.aptoide.pt.campaigns.data.PaECampaignsRepository
import cm.aptoide.pt.campaigns.data.paeCampaigns
import cm.aptoide.pt.usage_stats.PackageUsageManager
import com.aptoide.android.aptoidegames.MainActivity
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.play_and_earn.presentation.overlays.OverlayViewManager
import com.aptoide.android.aptoidegames.play_and_earn.presentation.permissions.hasOverlayPermission
import com.aptoide.android.aptoidegames.play_and_earn.presentation.permissions.hasUsageStatsPermissionStatus
import com.aptoide.android.aptoidegames.play_and_earn.presentation.sessions.PaESessionManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class PaEForegroundService : LifecycleService(), SavedStateRegistryOwner {

  @Inject
  lateinit var packageUsageManager: PackageUsageManager

  @Inject
  lateinit var overlayViewManager: OverlayViewManager

  @Inject
  //lateinit var paESessionManager: FakePaESessionManager
  lateinit var paESessionManager: PaESessionManager

  @Inject
  lateinit var paECampaignsRepository: PaECampaignsRepository

  private val savedStateRegistryController = SavedStateRegistryController.Companion.create(this)

  override val savedStateRegistry: SavedStateRegistry
    get() = savedStateRegistryController.savedStateRegistry

  private val pollingInterval = 6_000L
  private var pollingJob: Job? = null

  private var currentPackage: String? = null

  var paeApps = paeCampaigns.trending?.apps?.map { it.packageName } ?: emptyList()
  //var paeApps = emptyList<String>()

  override fun onCreate() {
    super.onCreate()

    savedStateRegistryController.performAttach()
    savedStateRegistryController.performRestore(null)
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    super.onStartCommand(intent, flags, startId)
    startForegroundWithNotification()
    startUsageMonitoring()
    return START_STICKY
  }

  private fun startForegroundWithNotification() {
    val notification = buildNotification()
    startForeground(FOREGROUND_SERVICE_ID, notification)
  }

  private fun startUsageMonitoring() {
    CoroutineScope(Dispatchers.IO).launch {
      paeApps = paECampaignsRepository.getCampaignPackages().getOrNull() ?: emptyList()
    }

    if (applicationContext.hasUsageStatsPermissionStatus() && applicationContext.hasOverlayPermission()) {
      paESessionManager.completedMissions.onEach {
        withContext(Dispatchers.Main) {
          overlayViewManager.showMissionCompletedOverlayView(
            it,
            this@PaEForegroundService,
            this@PaEForegroundService
          )
        }
      }.launchIn(CoroutineScope(Dispatchers.IO))

      pollingJob = CoroutineScope(Dispatchers.IO).launch {
        while (isActive) {
          val foregroundPackage = packageUsageManager.getForegroundPackage()
          if (foregroundPackage != currentPackage && foregroundPackage != null) {
            currentPackage = foregroundPackage

            if(foregroundPackage in paeApps) {
              paESessionManager.createSession(foregroundPackage)

              withContext(Dispatchers.Main) {
                overlayViewManager.showWelcomeBackOverlayView(
                  this@PaEForegroundService,
                  this@PaEForegroundService
                )
              }
            }
          }

          paESessionManager.syncSessions(currentPackage)

          delay(pollingInterval)
        }
      }
    }
  }

  private fun buildNotification(): Notification {
    setupNotificationChannel(applicationContext)

    val notification =
      NotificationCompat.Builder(applicationContext, PAE_USAGE_NOTIFICATION_CHANNEL_ID)
        .setContentTitle("Recording usage time")
        .setContentText("AptoideGames is recording app usage time")
        .setSmallIcon(R.drawable.notification_icon)
        .setContentIntent(
          PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
          )
        )
        .build()

    return notification
  }

  private fun setupNotificationChannel(context: Context) {
    val notificationManager =
      context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

    if (notificationManager.getNotificationChannel(PAE_USAGE_NOTIFICATION_CHANNEL_ID) == null) {
      val name = PAE_USAGE_NOTIFICATION_CHANNEL_NAME
      val descriptionText = "Play & Earn usage notification channel"
      val importance = NotificationManager.IMPORTANCE_DEFAULT
      val channel = NotificationChannel(
        PAE_USAGE_NOTIFICATION_CHANNEL_ID,
        name,
        importance
      ).apply {
        description = descriptionText
        setSound(null, null)
      }

      notificationManager.createNotificationChannel(channel)
    }
  }

  companion object {
    const val FOREGROUND_SERVICE_ID = 1001
    const val PAE_USAGE_NOTIFICATION_CHANNEL_ID = "pae_usage_notification_channel"
    const val PAE_USAGE_NOTIFICATION_CHANNEL_NAME = "Play & Earn Usage Notification Channel"
  }
}