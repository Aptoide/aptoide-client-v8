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
import cm.aptoide.pt.campaigns.data.PaECampaignsRepository
import cm.aptoide.pt.usage_stats.PackageUsageManager
import cm.aptoide.pt.usage_stats.PackageUsageState
import com.aptoide.android.aptoidegames.MainActivity
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.play_and_earn.PlayAndEarnManager
import com.aptoide.android.aptoidegames.play_and_earn.presentation.overlays.PaEOverlayViewManager
import com.aptoide.android.aptoidegames.play_and_earn.presentation.permissions.hasOverlayPermission
import com.aptoide.android.aptoidegames.play_and_earn.presentation.permissions.hasUsageStatsPermissionStatus
import com.aptoide.android.aptoidegames.play_and_earn.presentation.sessions.PaESessionManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class PaEForegroundService : LifecycleService(), SavedStateRegistryOwner {

  @Inject
  lateinit var packageUsageManager: PackageUsageManager

  @Inject
  lateinit var paeOverlayViewManager: PaEOverlayViewManager

  @Inject
  lateinit var paESessionManager: PaESessionManager

  @Inject
  lateinit var paECampaignsRepository: PaECampaignsRepository

  @Inject
  lateinit var playAndEarnManager: PlayAndEarnManager

  private val savedStateRegistryController = SavedStateRegistryController.Companion.create(this)

  override val savedStateRegistry: SavedStateRegistry
    get() = savedStateRegistryController.savedStateRegistry

  private val pollingIntervalMillis = 10_000L
  private val pollingIntervalSec = pollingIntervalMillis.toInt() / 1_000
  private var pollingJob: Job? = null
  private var completedMissionsJob: Job? = null
  private var isMonitoringStarted = false

  private var lastForegroundPackage: String? = null

  var availablePaEPackages: Set<String>? = null

  override fun onCreate() {
    super.onCreate()

    // Start foreground immediately to prevent crash if service gets stopped on initialization
    startForegroundWithNotification()

    savedStateRegistryController.performAttach()
    savedStateRegistryController.performRestore(null)

    observePlayAndEarnVisibility()
  }

  private fun observePlayAndEarnVisibility() {
    lifecycleScope.launch {
      playAndEarnManager.observePlayAndEarnVisibility().collect { isEnabled ->
        if (!isEnabled) {
          Timber.d("Feature flag disabled remotely, clearing sessions and stopping foreground service")
          paESessionManager.clearAllSessions()
          stopSelf()
        }
      }
    }
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    super.onStartCommand(intent, flags, startId)

    if (!applicationContext.hasUsageStatsPermissionStatus() || !applicationContext.hasOverlayPermission()) {
      stopSelf()
      return START_NOT_STICKY
    }

    init()

    return START_STICKY
  }

  private fun init() {
    // Start monitoring synchronously (same as original - needed for overlay lifecycle)
    if (!isMonitoringStarted) {
      startUsageMonitoring()
      isMonitoringStarted = true
    }

    // Check flag and fetch packages asynchronously to avoid ANR
    lifecycleScope.launch(Dispatchers.IO) {
      checkFlagAndFetchPackages()
    }
  }

  private suspend fun checkFlagAndFetchPackages() {
    try {
      // Check if feature is enabled remotely
      if (!playAndEarnManager.shouldShowPlayAndEarn()) {
        paESessionManager.clearAllSessions()
        stopSelf()
        return
      }

      // Fetch available packages (non-blocking)
      withTimeout(5000L) {
        availablePaEPackages = paECampaignsRepository.getAvailablePackages().getOrNull()
      }
    } catch (e: Exception) {
      Timber.e(e, "PaEForegroundService: checkFlagAndFetchPackages failed")
    }
  }

  private fun startForegroundWithNotification() {
    val notification = buildNotification()
    startForeground(FOREGROUND_SERVICE_ID, notification)
  }

  private fun startUsageMonitoring() {
    if (applicationContext.hasUsageStatsPermissionStatus() && applicationContext.hasOverlayPermission()) {
      completedMissionsJob?.cancel()
      completedMissionsJob = lifecycleScope.launch(Dispatchers.IO) {
        paESessionManager.completedMissions.collect {
          withContext(Dispatchers.Main) {
            paeOverlayViewManager.showMissionCompletedOverlayView(
              it,
              this@PaEForegroundService,
              this@PaEForegroundService
            )
          }
        }
      }

      pollingJob?.cancel()
      pollingJob = lifecycleScope.launch(Dispatchers.IO) {
        while (isActive) {
          syncService()
          delay(pollingIntervalMillis)
        }
      }
    }
  }

  private suspend fun syncService() {
    val activeSession = paESessionManager.activeSessions
      .firstOrNull { it.packageName == lastForegroundPackage }

    val packageState =
      packageUsageManager.getForegroundPackageState(activeSession?.lastAppOpenTime)

    when (packageState) {
      is PackageUsageState.ForegroundPackage -> {
        val foregroundPackage = packageState.packageName

        // New foreground package detected
        if (foregroundPackage != lastForegroundPackage) {
          activeSession?.pause()
          lastForegroundPackage = foregroundPackage

          // Game available in PaE. Start session
          if (availablePaEPackages?.contains(foregroundPackage) == true) {
            // Check if a session already exists and is not finished
            val sessionCreated = paESessionManager.createSession(foregroundPackage)

            // Always show welcome back overlay, even if session already exists
            if (sessionCreated || paESessionManager.activeSessions.any { it.packageName == foregroundPackage }) {
              withContext(Dispatchers.Main) {
                paeOverlayViewManager.showWelcomeBackOverlayView(
                  this@PaEForegroundService,
                  this@PaEForegroundService
                )
              }
            }
          }
        } else {
          // Same package still in foreground - sync active sessions
          paESessionManager.syncSessions(lastForegroundPackage, pollingIntervalSec)
        }
      }

      is PackageUsageState.NoForegroundPackage -> {
        // No package in foreground (e.g., screen locked, all apps paused)
        // Don't sync sessions as no time should be tracked while paused
        // Sessions will handle their own expiration via TTL
      }

      is PackageUsageState.Error -> {
        // Error means we couldn't determine state (no events in window or OEM failure)
        // Don't sync to avoid incorrectly counting time.
      }
    }
  }

  private fun buildNotification(): Notification {
    setupNotificationChannel(applicationContext)

    val notification =
      NotificationCompat.Builder(applicationContext, PAE_USAGE_NOTIFICATION_CHANNEL_ID)
        .setContentTitle(getString(R.string.play_and_earn_notification_recording_title))
        .setContentText(getString(R.string.play_and_earn_notification_recording_body))
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
    completedMissionsJob?.cancel()
    paESessionManager.clearAllSessions()
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
