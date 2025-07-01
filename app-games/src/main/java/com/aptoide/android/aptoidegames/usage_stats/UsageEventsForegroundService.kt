package com.aptoide.android.aptoidegames.usage_stats

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
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
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class UsageEventsForegroundService : LifecycleService(), SavedStateRegistryOwner {

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

  private fun calculateTotalTimeInForeground(usageStatsManager: UsageStatsManager): Long {
    val totalTime = 0L

    val startTime = System.currentTimeMillis() - 1000L * 60 * 60 * 24
    val endTime = System.currentTimeMillis()

    val usageEvents: UsageEvents = usageStatsManager.queryEvents(startTime, endTime)
    val event = UsageEvents.Event()

    val usageState =
      AppUsageState(packageName = "com.roblox.client", totalTime = 0L, classes = mutableMapOf())

    while (usageEvents.hasNextEvent()) {
      usageEvents.getNextEvent(event)

      if (event.packageName == "com.roblox.client") {
        when (event.eventType) {
          UsageEvents.Event.ACTIVITY_RESUMED -> {
            if (usageState.classes[event.className] == null) {
              usageState.classes[event.className] =
                ClassUsageState(isResumed = true, startTimestamp = event.timeStamp)
            }
          }

          UsageEvents.Event.ACTIVITY_PAUSED -> {
            val eventClass = usageState.classes[event.className]
            if (eventClass != null && eventClass.isResumed) {
              usageState.totalTime += event.timeStamp - eventClass.startTimestamp
              usageState.classes[event.className] =
                eventClass.copy(isResumed = false, startTimestamp = 0L)
            }
          }

          UsageEvents.Event.ACTIVITY_STOPPED -> {
            val eventClass = usageState.classes[event.className]
            if (eventClass != null && eventClass.isResumed) {
              usageState.totalTime += event.timeStamp - eventClass.startTimestamp
            }
            usageState.classes.remove(event.className)
          }
        }
      }
    }

    return usageState.totalTime
  }

  private fun calculateTimeInForeground(usageStatsManager: UsageStatsManager) {
    val startTime = System.currentTimeMillis() - 1000L * 60 * 60
    val endTime = System.currentTimeMillis()

    val usageEvents: UsageEvents = usageStatsManager.queryEvents(startTime, endTime)

    val event = UsageEvents.Event()

    while (usageEvents.hasNextEvent()) {
      usageEvents.getNextEvent(event)

      if (event.packageName == "com.roblox.client") {
        //Timber.tag("usage_event").d("EVENT: ${event.eventType}; ${event.className}; ${event.timeStamp.toDateString()}")

        when (event.eventType) {
          UsageEvents.Event.ACTIVITY_RESUMED -> {
            Timber.tag("usage_event")
              .d("ACTIVITY_RESUMED: ${event.className}; ${event.timeStamp.toDateString()}")
          }

          UsageEvents.Event.ACTIVITY_PAUSED -> {
            Timber.tag("usage_event")
              .d("ACTIVITY_PAUSED: ${event.className}; ${event.timeStamp.toDateString()}")
          }

          UsageEvents.Event.ACTIVITY_STOPPED -> {
            Timber.tag("usage_event")
              .d("ACTIVITY_STOPPED: ${event.className}; ${event.timeStamp.toDateString()}")
          }
        }
      }
    }

    Timber.tag("usage_event").d("-------------------------------------------------------")
    Timber.tag("usage_event").d("-------------------------------------------------------")
  }

  private fun startUsageStatsTracking() {
    val usageStatsManager =
      applicationContext.getSystemService(USAGE_STATS_SERVICE) as UsageStatsManager


    if (hasUsageStatsPermission()) {
      pollingJob = CoroutineScope(Dispatchers.IO).launch {
        while (isActive) {

          val totalTimeEvents = calculateTotalTimeInForeground(usageStatsManager)

          val totalTimeStats = packageUsageStatsProvider.getTotalTimeInForeground("com.roblox.client")

          Timber.tag("usage_event").d("Total time foreground events: $totalTimeEvents")

          Timber.tag("usage_event").d("Total time foreground stats: $totalTimeStats")

          delay(pollingInterval)
        }
      }
    }
  }

  private fun Long.toDateString(): String {
    val date = Date(this)
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    return sdf.format(date)
  }

  private fun updateOverlayUI(timeInForeground: Long) {
    val view = getUsageStatsView(applicationContext, timeInForeground)

    view.apply {
      setViewTreeSavedStateRegistryOwner(this@UsageEventsForegroundService)
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

data class AppUsageState(
  val packageName: String,
  var totalTime: Long = 0,
  val classes: MutableMap<String, ClassUsageState>,
)

data class ClassUsageState(
  var isResumed: Boolean,
  val startTimestamp: Long
)

