package com.aptoide.android.aptoidegames.usage_stats

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.view.WindowManager
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.aptoide.android.aptoidegames.MainActivity
import com.aptoide.android.aptoidegames.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class PackageUsageStatsForegroundService : LifecycleService(), SavedStateRegistryOwner {

  private val savedStateRegistryController = SavedStateRegistryController.create(this)

  override val savedStateRegistry: SavedStateRegistry
    get() = savedStateRegistryController.savedStateRegistry

  @Inject
  lateinit var packageUsageStatsProvider: PackageUsageStatsProvider

  private val pollingInterval = 15_000L  // 10 seconds
  private var pollingJob: Job? = null

  override fun onCreate() {
    super.onCreate()

    savedStateRegistryController.performAttach() // you can ignore this line, becase performRestore method will auto call performAttach() first.
    savedStateRegistryController.performRestore(null)
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    super.onStartCommand(intent, flags, startId)
    startForegroundWithNotification()
    startUsageStatsTracking()
    return START_STICKY
  }

  private fun startForegroundWithNotification() {
    val notification = buildNotification()

    startForeground(1234, notification)
  }

  override fun onDestroy() {
    super.onDestroy()
    pollingJob?.cancel()
  }

  private fun startUsageStatsTracking() {
    if (hasUsageStatsPermission()) {
      pollingJob = CoroutineScope(Dispatchers.IO).launch {
        while (isActive) {
          val totalTimeInForeground =
            packageUsageStatsProvider.getTotalTimeInForeground("com.roblox.client")

          totalTimeInForeground?.let {
            withContext(Dispatchers.Main) {
              updateOverlayUI(it)
            }
          }

          delay(pollingInterval)
        }
      }
    }
  }

  private fun updateOverlayUI(timeInForeground: Long) {
    val view = getUsageStatsView(applicationContext, timeInForeground)

    view.apply {
      setViewTreeSavedStateRegistryOwner(this@PackageUsageStatsForegroundService)
    }

    val windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
    val params = WindowManager.LayoutParams(
      WindowManager.LayoutParams.WRAP_CONTENT,
      WindowManager.LayoutParams.WRAP_CONTENT,
      WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
      WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
      PixelFormat.TRANSLUCENT
    )

    println("AAAAA adding view")

    windowManager.addView(view, params)
  }

  private fun buildNotification(): Notification {
    setupNotificationChannel(applicationContext)

    val notification =
      NotificationCompat.Builder(applicationContext, USAGE_STATS_NOTIFICATION_CHANNEL_ID)
        .setContentTitle("Usage Stats Tracking")
        .setContentText("We are tracking usage stats")
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

    if (notificationManager.getNotificationChannel(USAGE_STATS_NOTIFICATION_CHANNEL_ID) == null) {
      val name = USAGE_STATS_NOTIFICATION_CHANNEL_NAME
      val descriptionText = "Usage stats notification channel"
      val importance = NotificationManager.IMPORTANCE_DEFAULT
      val channel = NotificationChannel(
        USAGE_STATS_NOTIFICATION_CHANNEL_ID,
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
    const val USAGE_STATS_NOTIFICATION_CHANNEL_ID = "usage_stats_notification_channel"
    const val USAGE_STATS_NOTIFICATION_CHANNEL_NAME = "Usage Stats Notification Channel"
  }
}
