package com.aptoide.android.aptoidegames.play_and_earn.presentation.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.PowerManager
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
import com.aptoide.android.aptoidegames.play_and_earn.data.PaEPreferencesRepository
import com.aptoide.android.aptoidegames.play_and_earn.domain.sessions.PaEStatusHeartbeatPolicy
import com.aptoide.android.aptoidegames.play_and_earn.presentation.notifications.PaEMissionCompletedNotificationBuilder
import com.aptoide.android.aptoidegames.play_and_earn.presentation.notifications.missionCompletedRoute
import com.aptoide.android.aptoidegames.play_and_earn.presentation.overlays.PaEOverlayViewManager
import com.aptoide.android.aptoidegames.play_and_earn.presentation.permissions.hasOverlayPermission
import com.aptoide.android.aptoidegames.play_and_earn.presentation.permissions.hasUsageStatsPermissionStatus
import com.aptoide.android.aptoidegames.play_and_earn.presentation.sessions.PaESessionManager
import com.aptoide.android.aptoidegames.putNotificationRoute
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class PaEForegroundService : LifecycleService(), SavedStateRegistryOwner {

  @Inject
  lateinit var packageUsageManager: PackageUsageManager

  @Inject
  lateinit var paeOverlayViewManager: PaEOverlayViewManager

  @Inject
  lateinit var paEMissionCompletedNotificationBuilder: PaEMissionCompletedNotificationBuilder

  @Inject
  lateinit var paESessionManager: PaESessionManager

  @Inject
  lateinit var paECampaignsRepository: PaECampaignsRepository

  @Inject
  lateinit var playAndEarnManager: PlayAndEarnManager

  @Inject
  lateinit var paEPreferencesRepository: PaEPreferencesRepository

  private val savedStateRegistryController = SavedStateRegistryController.Companion.create(this)

  override val savedStateRegistry: SavedStateRegistry
    get() = savedStateRegistryController.savedStateRegistry

  private val pollingIntervalMillis = 10_000L
  private val pollingIntervalSec = pollingIntervalMillis.toInt() / 1_000
  private var pollingJob: Job? = null
  private var completedMissionsJob: Job? = null
  private var idleTimeoutJob: Job? = null
  private val idleTimeoutMillis = 5 * 60 * 1000L // 30 minutes
  private var isMonitoringStarted = false
  private var flagsJob: Job? = null

  private var lastForegroundPackage: String? = null

  var availablePaEPackages: Set<String>? = null

  // Status mode (AND-879): heartbeats for one game after Play, without usage tracking.
  private var statusPackage: String? = null
  private var statusJob: Job? = null
  @Volatile
  private var statusPolicy: PaEStatusHeartbeatPolicy? = null
  private var screenReceiver: BroadcastReceiver? = null

  override fun onCreate() {
    super.onCreate()

    // Start foreground immediately to prevent crash if service gets stopped on initialization
    startForegroundWithNotification()

    savedStateRegistryController.performAttach()
    savedStateRegistryController.performRestore(null)
  }

  /**
   * Stops the service when PaE is disabled remotely. Usage tracking being switched off also stops
   * the tracking mode, but not the status mode, which exists for exactly that case.
   */
  private fun observePlayAndEarnFlags(statusMode: Boolean) {
    flagsJob?.cancel()
    flagsJob = lifecycleScope.launch {
      combine(
        playAndEarnManager.observePlayAndEarnVisibility(),
        playAndEarnManager.observeUsageTrackingEnabled(),
      ) { isVisible, isTrackingEnabled -> isVisible && (statusMode || isTrackingEnabled) }
        .collect { isEnabled ->
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

    intent?.getStringExtra(EXTRA_STATUS_PACKAGE)?.let { packageName ->
      // Tracking mode already watches the foreground game and announces completions itself.
      if (isMonitoringStarted) return START_STICKY
      observePlayAndEarnFlags(statusMode = true)
      startStatusMode(packageName)
      // Not restarted by the system: a lost process means no heartbeats until the next Play.
      return START_NOT_STICKY
    }

    // A tracking start replaces a running status mode.
    statusJob?.cancel()
    statusPackage?.let { previous ->
      lifecycleScope.launch(Dispatchers.IO) { paESessionManager.endSession(previous) }
    }
    statusPackage = null
    observePlayAndEarnFlags(statusMode = false)
    if (!applicationContext.hasUsageStatsPermissionStatus() || !applicationContext.hasOverlayPermission()) {
      stopSelf()
      return START_NOT_STICKY
    }

    // Ensure preference reflects the service is running (e.g. when started from Play button)
    lifecycleScope.launch(Dispatchers.IO) {
      paEPreferencesRepository.setPaEServiceEnabled(true)
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
      // Check if feature (visibility + usage tracking) is enabled remotely
      if (!playAndEarnManager.shouldRunUsageTracking()) {
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

  /** Announces every mission the server confirms, in both modes. */
  private fun collectCompletedMissions() {
    if (completedMissionsJob?.isActive == true) return
    completedMissionsJob = lifecycleScope.launch(Dispatchers.IO) {
      paESessionManager.completedMissions.collect { event ->
        // One bad icon download or notify() failure must not cancel the collector: the flow has
        // no replay, and the collector is not re-run, so it would never recover.
        runCatching {
          paEMissionCompletedNotificationBuilder.showMissionCompletedNotification(
            mission = event.mission,
            packageName = event.packageName
          )
        }.onFailure { Timber.e(it, "PaEForegroundService: mission notification failed") }
      }
    }
  }

  // ---------------------------------------------------------------- status mode (AND-879)

  /**
   * After Play, with usage tracking off: a zero-second heartbeat every interval, only to learn
   * which missions the developer's MMP confirmed. The app can't see when the game leaves the
   * foreground (no usage-access permission), so it stops when nothing is left to wait for, after
   * a while with the screen off, or at a long backstop; see [PaEStatusHeartbeatPolicy].
   */
  private fun startStatusMode(packageName: String) {
    val previousJob = statusJob?.also { it.cancel() }
    val previousPackage = statusPackage?.takeIf { it != packageName }
    statusPackage = packageName
    startForeground(FOREGROUND_SERVICE_ID, buildStatusNotification(packageName))
    collectCompletedMissions()

    // Created before anything suspends, so a screen-off during start-up isn't lost, and seeded
    // from the current screen state. The backstop is raised once remote config is read.
    val policy = PaEStatusHeartbeatPolicy(
      startedAtMillis = System.currentTimeMillis(),
      screenOffLimitMillis = STATUS_SCREEN_OFF_LIMIT_MILLIS,
      maxDurationMillis = TimeUnit.HOURS.toMillis(1),
    )
    if ((getSystemService(POWER_SERVICE) as PowerManager).isInteractive.not()) {
      policy.onScreenOff(System.currentTimeMillis())
    }
    statusPolicy = policy
    registerScreenReceiver()

    statusJob = lifecycleScope.launch(Dispatchers.IO) {
      // The sessions list is shared: the previous run must be over before this one touches it.
      previousJob?.join()
      previousPackage?.let { paESessionManager.endSession(it) }
      if (!playAndEarnManager.shouldShowPlayAndEarn() || !playAndEarnManager.isSignedIn()) {
        stopStatusMode("not available")
        return@launch
      }
      policy.maxDurationMillis =
        TimeUnit.HOURS.toMillis(playAndEarnManager.getStatusHeartbeatMaxHours())
      val intervalMillis = paEPreferencesRepository.getHeartbeatIntervalSeconds() * 1000L
      var lastRefreshMillis = System.currentTimeMillis()
      var sessionFailures = 0

      while (isActive) {
        val now = System.currentTimeMillis()
        val session =
          paESessionManager.activeSessions.firstOrNull { it.packageName == packageName }
        policy.stopReason(session?.missions, session?.completedMissions?.toSet().orEmpty(), now)
          ?.let { reason ->
            stopStatusMode(reason.name)
            return@launch
          }
        if (!policy.isPaused) {
          // A session the server dropped (or that never got created) is retried next interval;
          // a bad connection shouldn't end the run, but a persistent failure should.
          val alive =
            ensureSession(packageName) && paESessionManager.heartbeatStatus(packageName)
          sessionFailures = if (alive) 0 else sessionFailures + 1
          if (sessionFailures >= STATUS_MAX_SESSION_FAILURES) {
            stopStatusMode("session lost")
            return@launch
          }
          if (alive && now - lastRefreshMillis >= STATUS_MISSIONS_REFRESH_MILLIS) {
            paESessionManager.refreshMissions(packageName)
            lastRefreshMillis = now
          }
          // A heartbeat that confirmed the last mission ends the run now, not an interval later.
          val after =
            paESessionManager.activeSessions.firstOrNull { it.packageName == packageName }
          policy.stopReason(after?.missions, after?.completedMissions?.toSet().orEmpty(), now)
            ?.let { reason ->
              stopStatusMode(reason.name)
              return@launch
            }
        }
        delay(intervalMillis)
      }
    }
  }

  private suspend fun ensureSession(packageName: String): Boolean =
    paESessionManager.activeSessions.any { it.packageName == packageName } ||
      paESessionManager.createSession(packageName)

  private suspend fun stopStatusMode(reason: String) {
    Timber.d("PaEForegroundService: status heartbeat for $statusPackage stops ($reason)")
    statusPackage?.let { paESessionManager.endSession(it) }
    statusPackage = null
    stopSelf()
  }

  private fun registerScreenReceiver() {
    if (screenReceiver != null) return
    val receiver = object : BroadcastReceiver() {
      override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
          Intent.ACTION_SCREEN_OFF -> statusPolicy?.onScreenOff(System.currentTimeMillis())
          Intent.ACTION_SCREEN_ON -> statusPolicy?.onScreenOn()
        }
      }
    }
    val filter = IntentFilter().apply {
      addAction(Intent.ACTION_SCREEN_OFF)
      addAction(Intent.ACTION_SCREEN_ON)
    }
    ContextCompat.registerReceiver(this, receiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED)
    screenReceiver = receiver
  }

  private fun unregisterScreenReceiver() {
    screenReceiver?.let { runCatching { unregisterReceiver(it) } }
    screenReceiver = null
  }

  private fun buildStatusNotification(packageName: String): Notification {
    setupNotificationChannel(applicationContext)
    val openGameRewards = Intent(this, MainActivity::class.java).apply {
      putNotificationRoute(missionCompletedRoute(packageName))
      flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }
    return NotificationCompat.Builder(applicationContext, PAE_USAGE_NOTIFICATION_CHANNEL_ID)
      .setContentTitle(getString(R.string.play_and_earn_notification_status_title))
      .setContentText(getString(R.string.play_and_earn_notification_status_body))
      .setSmallIcon(R.drawable.notification_icon)
      .setContentIntent(
        PendingIntent.getActivity(
          this,
          1,
          openGameRewards,
          PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
      )
      .build()
  }

  // ------------------------------------------------------------- usage-tracking mode

  private fun startUsageMonitoring() {
    if (applicationContext.hasUsageStatsPermissionStatus() && applicationContext.hasOverlayPermission()) {
      collectCompletedMissions()

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
        val isPaEGame = availablePaEPackages?.contains(foregroundPackage) == true

        // Manage idle timeout based on whether user is playing a PaE game
        if (isPaEGame) {
          cancelIdleTimeout()
        } else {
          startIdleTimeoutIfNotRunning()
        }

        // New foreground package detected
        if (foregroundPackage != lastForegroundPackage) {
          activeSession?.pause()
          lastForegroundPackage = foregroundPackage

          // Game available in PaE. Start session
          if (isPaEGame) {
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
        startIdleTimeoutIfNotRunning()
      }

      is PackageUsageState.Error -> {
        // Error means we couldn't determine state (no events in window or OEM failure)
        // Don't sync to avoid incorrectly counting time.
        startIdleTimeoutIfNotRunning()
      }
    }
  }

  private fun buildNotification(): Notification {
    setupNotificationChannel(applicationContext)

    val settingsIntent = Intent(this, MainActivity::class.java).apply {
      putExtra(NAVIGATE_TO_SETTINGS, true)
      flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }

    val notification =
      NotificationCompat.Builder(applicationContext, PAE_USAGE_NOTIFICATION_CHANNEL_ID)
        .setContentTitle(getString(R.string.play_and_earn_notification_recording_title))
        .setContentText(getString(R.string.play_and_earn_notification_recording_body))
        .setSmallIcon(R.drawable.notification_icon)
        .setContentIntent(
          PendingIntent.getActivity(
            this,
            0,
            settingsIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
          )
        )
        .build()

    return notification
  }

  private fun cancelIdleTimeout() {
    idleTimeoutJob?.cancel()
    idleTimeoutJob = null
  }

  private fun startIdleTimeoutIfNotRunning() {
    if (idleTimeoutJob?.isActive == true) {
      return // Already running
    }
    idleTimeoutJob = lifecycleScope.launch {
      Timber.d("PaEForegroundService: Starting idle timeout ($idleTimeoutMillis ms)")
      delay(idleTimeoutMillis)
      Timber.d("PaEForegroundService: Idle timeout elapsed, stopping service")
      stopSelf()
    }
  }

  private fun setupNotificationChannel(context: Context) {
    val notificationManager =
      context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

    if (notificationManager.getNotificationChannel(PAE_USAGE_NOTIFICATION_CHANNEL_ID) == null) {
      val name = PAE_USAGE_NOTIFICATION_CHANNEL_NAME
      val descriptionText = "Play & Earn usage notification channel"
      val importance = NotificationManager.IMPORTANCE_LOW
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
    statusJob?.cancel()
    flagsJob?.cancel()
    completedMissionsJob?.cancel()
    idleTimeoutJob?.cancel()
    unregisterScreenReceiver()
    paESessionManager.clearAllSessions()
    isMonitoringStarted = false
  }

  companion object {
    const val FOREGROUND_SERVICE_ID = 1001
    const val PAE_USAGE_NOTIFICATION_CHANNEL_ID = "pae_usage_notification_channel"
    const val PAE_USAGE_NOTIFICATION_CHANNEL_NAME = "Play & Earn Usage Notification Channel"
    const val NAVIGATE_TO_SETTINGS = "navigate_to_settings"
    private const val EXTRA_STATUS_PACKAGE = "pae_status_package"
    private val STATUS_SCREEN_OFF_LIMIT_MILLIS = TimeUnit.MINUTES.toMillis(10)
    private val STATUS_MISSIONS_REFRESH_MILLIS = TimeUnit.MINUTES.toMillis(5)
    private const val STATUS_MAX_SESSION_FAILURES = 3

    /** Status mode (AND-879): heartbeats for [packageName] after Play, no usage tracking. */
    fun startStatusHeartbeat(context: Context, packageName: String) {
      try {
        val serviceIntent = Intent(context, PaEForegroundService::class.java)
          .putExtra(EXTRA_STATUS_PACKAGE, packageName)
        ContextCompat.startForegroundService(context, serviceIntent)
      } catch (e: Throwable) {
        Timber.e(e, "PaEForegroundService: could not start the status heartbeat")
      }
    }

    fun start(context: Context) {
      try {
        val serviceIntent = Intent(context, PaEForegroundService::class.java)
        ContextCompat.startForegroundService(context, serviceIntent)
      } catch (e: Throwable) {
        e.printStackTrace()
      }
    }

    fun stop(context: Context) {
      try {
        val serviceIntent = Intent(context, PaEForegroundService::class.java)
        context.stopService(serviceIntent)
      } catch (e: Throwable) {
        e.printStackTrace()
      }
    }
  }
}
