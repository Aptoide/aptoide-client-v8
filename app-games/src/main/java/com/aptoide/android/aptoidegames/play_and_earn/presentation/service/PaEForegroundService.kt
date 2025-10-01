package com.aptoide.android.aptoidegames.play_and_earn.presentation.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import com.aptoide.android.aptoidegames.MainActivity
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.play_and_earn.presentation.permissions.hasOverlayPermission
import com.aptoide.android.aptoidegames.play_and_earn.presentation.permissions.hasUsageStatsPermissionStatus
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PaEForegroundService : LifecycleService(), SavedStateRegistryOwner {

  private val savedStateRegistryController = SavedStateRegistryController.Companion.create(this)

  override val savedStateRegistry: SavedStateRegistry
    get() = savedStateRegistryController.savedStateRegistry

  private val pollingInterval = 6_000L
  private var pollingJob: Job? = null
  private var isMonitoringStarted = false

  override fun onCreate() {
    super.onCreate()

    savedStateRegistryController.performAttach()
    savedStateRegistryController.performRestore(null)
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    super.onStartCommand(intent, flags, startId)
    startForegroundWithNotification()

    if (!isMonitoringStarted) {
      startUsageMonitoring()
      isMonitoringStarted = true
    }
    return START_STICKY
  }

  private fun startForegroundWithNotification() {
    val notification = buildNotification()
    startForeground(FOREGROUND_SERVICE_ID, notification)
  }

  private fun startUsageMonitoring() {
    if (applicationContext.hasUsageStatsPermissionStatus() && applicationContext.hasOverlayPermission()) {
      pollingJob?.cancel()
      pollingJob = lifecycleScope.launch(Dispatchers.IO) {
        while (isActive) {
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

  override fun onDestroy() {
    super.onDestroy()
    pollingJob?.cancel()
    isMonitoringStarted = false
  }

  companion object {
    const val FOREGROUND_SERVICE_ID = 1001
    const val PAE_USAGE_NOTIFICATION_CHANNEL_ID = "pae_usage_notification_channel"
    const val PAE_USAGE_NOTIFICATION_CHANNEL_NAME = "Play & Earn Usage Notification Channel"

    fun start(context: Context) {
      try {
        val serviceIntent = Intent(context, PaEForegroundService::class.java)
        ContextCompat.startForegroundService(context, serviceIntent)
      } catch (e: Throwable) {
        e.printStackTrace()
      }
    }
  }
}
