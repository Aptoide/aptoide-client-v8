package com.aptoide.android.aptoidegames.gamegenie.presentation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.IBinder
import android.view.WindowManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class GameGenieOverlayService : Service(),
  LifecycleOwner,
  ViewModelStoreOwner,
  SavedStateRegistryOwner {

  companion object {
    private const val NOTIFICATION_ID = 1
    private const val CHANNEL_ID = "gamegenie_overlay_channel"
    private const val CHANNEL_NAME = "GameGenie Overlay Service"

    const val EXTRA_MEDIA_PROJECTION_RESULT_CODE = "MEDIA_PROJECTION_RESULT_CODE"
    const val EXTRA_MEDIA_PROJECTION_DATA = "MEDIA_PROJECTION_DATA"
    const val EXTRA_REQUEST_MEDIA_PROJECTION = "REQUEST_MEDIA_PROJECTION"

    private const val ANIMATION_DURATION_MS = 300L
    private const val ANIMATION_FRAME_DELAY_MS = 16L

    private const val SCREENSHOT_DELAY_MS = 100L

    @Volatile
    var isServiceRunning: Boolean = false
      private set
    
    @Volatile
    var hasScreenshotPermission: Boolean = false
      private set
    
    @Volatile
    var needsMediaProjectionPermission: Boolean = false
    
    fun clearPermissionRequest() {
      needsMediaProjectionPermission = false
    }
  }

  private val lifecycleRegistry = LifecycleRegistry(this)
  private val myViewModelStore: ViewModelStore = ViewModelStore()
  private val savedStateRegistryController: SavedStateRegistryController by lazy {
    SavedStateRegistryController.create(this)
  }

  override val savedStateRegistry: SavedStateRegistry
    get() = savedStateRegistryController.savedStateRegistry
  override val lifecycle: Lifecycle get() = lifecycleRegistry
  override val viewModelStore: ViewModelStore get() = myViewModelStore

  private lateinit var windowManager: OverlayWindowManager
  private lateinit var screenshotManager: OverlayScreenshotManager
  private lateinit var displayMonitor: OverlayDisplayMonitor

  private val overlayView: ComposeView by lazy { ComposeView(this) }
  private var menuOverlayView: ComposeView? = null

  private val scope = CoroutineScope(Dispatchers.Main + Job())

  private var onOrientationChanged: ((Int, Int) -> Unit)? = null

  private var pendingMediaProjectionResultCode: Int = 0
  private var pendingMediaProjectionData: Intent? = null

  override fun onBind(intent: Intent?): IBinder? = null

  override fun onCreate() {
    super.onCreate()
    isServiceRunning = true
    startAsForegroundService()

    try {
      initializeLifecycle()
      initializeManagers()
      setupOverlayView()
      startMonitoring()
    } catch (e: Exception) {
      e.printStackTrace()
      stopSelf()
    }
  }

  private fun initializeLifecycle() {
    savedStateRegistryController.performAttach()
    savedStateRegistryController.performRestore(null)
    lifecycleRegistry.currentState = Lifecycle.State.STARTED

    lifecycleRegistry.addObserver(object : LifecycleEventObserver {
      override fun onStateChanged(
        source: LifecycleOwner,
        event: Lifecycle.Event,
      ) {
        if (source.lifecycle.currentState == Lifecycle.State.DESTROYED) {
          viewModelStore.clear()
          source.lifecycle.removeObserver(this)
        }
      }
    })
  }

  private fun initializeManagers() {
    windowManager = OverlayWindowManager(this)
    screenshotManager = OverlayScreenshotManager(this)
    
    screenshotManager.onMediaProjectionStopped = {
      hasScreenshotPermission = false
    }

    displayMonitor = OverlayDisplayMonitor(
      context = this,
      scope = scope,
      onDimensionChanged = { handleDimensionChange() }
    )
  }

  private fun setupOverlayView() {
    val entryPoint = EntryPointAccessors.fromApplication(
      this,
      GameGenieViewModelDependencies::class.java
    )
    val analytics = entryPoint.gameGenieAnalytics()

    val params = windowManager.createFabLayoutParams()

    overlayView.apply {
      setViewTreeLifecycleOwner(this@GameGenieOverlayService)
      setViewTreeViewModelStoreOwner(this@GameGenieOverlayService)
      setViewTreeSavedStateRegistryOwner(this@GameGenieOverlayService)

      var fabX by mutableIntStateOf(params.x)
      var fabY by mutableIntStateOf(params.y)
      var showMenu by mutableStateOf(false)

      onOrientationChanged = { newX, newY ->
        fabX = newX
        fabY = newY
      }

      val onMenuToggle: () -> Unit = {
        val newMenuState = !showMenu

        if (newMenuState) {
          showMenu = true
          val layoutParams = overlayView.layoutParams as WindowManager.LayoutParams
          val actualX = layoutParams.x
          val actualY = layoutParams.y
          showMenuWindow(actualX, actualY, actualX < windowManager.screenWidth / 2, analytics)
        } else {
          showMenu = false
          hideMenuWindow()
        }
      }

      setContent {
        val fabSize = OverlayWindowManager.FAB_SIZE_DP.dp
        val fabSizePx = with(LocalDensity.current) { fabSize.toPx().toInt() }

        AptoideTheme {
          GameGenieOverlay(
            showMenu = showMenu,
            onMenuToggle = onMenuToggle,
            onDrag = { dx, dy ->
              fabX = (fabX + dx)
                .coerceIn(
                  windowManager.getEdgePaddingPx(),
                  windowManager.screenWidth - fabSizePx - windowManager.getEdgePaddingPx()
                )

              val maxYForMenu = windowManager.screenHeight - fabSizePx -
                windowManager.getMenuSpacingPx() - windowManager.getMenuHeightPx()

              fabY = (fabY + dy)
                .coerceIn(windowManager.getEdgePaddingPx(), maxYForMenu)

              val layoutParams = overlayView.layoutParams as WindowManager.LayoutParams
              layoutParams.x = fabX
              layoutParams.y = fabY
              windowManager.updateViewLayout(overlayView, layoutParams)
            },
            onDragEnd = {
              scope.launch {
                val startX = fabX
                val middle = windowManager.screenWidth / 2
                val targetX = if (startX + fabSizePx / 2 < middle) {
                  windowManager.getEdgePaddingPx()
                } else {
                  windowManager.screenWidth - fabSizePx - windowManager.getEdgePaddingPx()
                }

                animateToX(startX, targetX) { newX ->
                  fabX = newX
                  val layoutParams = overlayView.layoutParams as WindowManager.LayoutParams
                  layoutParams.x = newX
                  windowManager.updateViewLayout(overlayView, layoutParams)
                }
              }
            },
            onScreenshotRequest = {
              takeScreenshot()
            },
            onMenuClosed = {
              hideMenuWindow()
              showMenu = false
            },
            analytics = analytics
          )
        }
      }
    }

    windowManager.addView(overlayView, params)
    lifecycleRegistry.currentState = Lifecycle.State.RESUMED
  }

  private fun startMonitoring() {
    displayMonitor.startMonitoring(windowManager.screenWidth, windowManager.screenHeight)
    displayMonitor.performForcedChecks()
  }

  private fun handleDimensionChange() {
    windowManager.updateScreenDimensions()

    if (overlayView.parent != null) {
      val layoutParams = overlayView.layoutParams as WindowManager.LayoutParams
      val (newX, newY) = windowManager.adjustPositionForOrientationChange(layoutParams.x, layoutParams.y)

      if (newX != layoutParams.x || newY != layoutParams.y) {
        layoutParams.x = newX
        layoutParams.y = newY
        windowManager.updateViewLayout(overlayView, layoutParams)

        onOrientationChanged?.invoke(newX, newY)
      }
    }
  }

  override fun onConfigurationChanged(newConfig: android.content.res.Configuration) {
    super.onConfigurationChanged(newConfig)
    handleDimensionChange()
  }

  override fun onStartCommand(
    intent: Intent?,
    flags: Int,
    startId: Int,
  ): Int {
    super.onStartCommand(intent, flags, startId)

    val newResultCode = intent?.getIntExtra(EXTRA_MEDIA_PROJECTION_RESULT_CODE, 0) ?: 0
    val newData: Intent? =
      if (Build.VERSION.SDK_INT >= 33) {
        intent?.getParcelableExtra(EXTRA_MEDIA_PROJECTION_DATA, Intent::class.java)
      } else {
        @Suppress("DEPRECATION")
        intent?.getParcelableExtra(EXTRA_MEDIA_PROJECTION_DATA)
      }

    if (newResultCode != 0 && newData != null) {
      pendingMediaProjectionResultCode = newResultCode
      pendingMediaProjectionData = newData
      hasScreenshotPermission = true
    } else if (screenshotManager.hasPermissionData()) {
      if (screenshotManager.needsRecreation()) {
        if (screenshotManager.hasMediaProjection()) {
          screenshotManager.setupVirtualDisplay(
            windowManager.screenWidth,
            windowManager.screenHeight,
            onFirstFrameReady = {
            }
          )
        }
      }
    }

    return START_STICKY
  }

  private fun showMenuWindow(
    fabX: Int,
    fabY: Int,
    isFabOnLeftSide: Boolean,
    analytics: com.aptoide.android.aptoidegames.gamegenie.analytics.GameGenieAnalytics,
  ) {
    hideMenuWindow()

    val menuView = ComposeView(this).apply {
      setViewTreeLifecycleOwner(this@GameGenieOverlayService)
      setViewTreeViewModelStoreOwner(this@GameGenieOverlayService)
      setViewTreeSavedStateRegistryOwner(this@GameGenieOverlayService)

      setContent {
        AptoideTheme {
          GameGenieMenu(
            onAskAnything = {
              analytics.sendGameGenieOverlayAskAnything()
              hideMenuWindow()
              returnToAptoideGames()
            },
            onScreenshot = {
              analytics.sendGameGenieOverlayScreenshot()
              hideMenuWindow()
              takeScreenshot()
            },
            onCloseOverlay = {
              analytics.sendGameGenieOverlayRemove()
              hideMenuWindow()
              stopService(Intent(this@GameGenieOverlayService, GameGenieOverlayService::class.java))
            }
          )
        }
      }
    }

    val fabSizePx = windowManager.getFabSizePx()
    val spacingPx = windowManager.getMenuSpacingPx()

    val menuX = if (isFabOnLeftSide) {
      fabX
    } else {
      fabX - windowManager.getMenuLeftExtensionPx()
    }
    val menuY = fabY + fabSizePx + spacingPx

    val menuParams = WindowManager.LayoutParams(
      windowManager.getMenuWidthPx(),
      windowManager.getMenuHeightPx(),
      WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
      WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
      android.graphics.PixelFormat.TRANSLUCENT
    ).apply {
      gravity = android.view.Gravity.TOP or android.view.Gravity.START
      x = menuX
      y = menuY
    }

    windowManager.addView(menuView, menuParams)
    menuOverlayView = menuView
  }

  private fun hideMenuWindow() {
    menuOverlayView?.let {
      windowManager.removeView(it)
      menuOverlayView = null
    }
  }

  private suspend fun animateToX(
    startX: Int,
    targetX: Int,
    onUpdate: (Int) -> Unit,
  ) {
    val steps = (ANIMATION_DURATION_MS / ANIMATION_FRAME_DELAY_MS).toInt()
    val delta = targetX - startX

    for (i in 1..steps) {
      val progress = i.toFloat() / steps
      val newX = startX + (delta * progress).toInt()
      onUpdate(newX)
      delay(ANIMATION_FRAME_DELAY_MS)
    }

    onUpdate(targetX)
  }

  private fun returnToAptoideGames() {
    val intent = packageManager.getLaunchIntentForPackage(packageName)
    intent?.apply {
      addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
      startActivity(this)
    }
  }
  
  private fun returnToAptoideGamesForPermission() {
    needsMediaProjectionPermission = true
    val intent = packageManager.getLaunchIntentForPackage(packageName)
    intent?.apply {
      addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
      putExtra(EXTRA_REQUEST_MEDIA_PROJECTION, true)
      startActivity(this)
    }
  }

  private fun takeScreenshot() {
    var deferredSetupPerformed = false
    if (!screenshotManager.hasMediaProjection() && pendingMediaProjectionResultCode != 0 && pendingMediaProjectionData != null) {
      screenshotManager.setupMediaProjection(pendingMediaProjectionResultCode, pendingMediaProjectionData)
      
      if (screenshotManager.hasMediaProjection()) {
        screenshotManager.setupVirtualDisplay(
          windowManager.screenWidth,
          windowManager.screenHeight
        )
        pendingMediaProjectionResultCode = 0
        pendingMediaProjectionData = null
        deferredSetupPerformed = true
      } else {
        hasScreenshotPermission = false
      }
    }
    
    if (!screenshotManager.hasMediaProjection()) {
      hasScreenshotPermission = false
      return
    }
    
    synchronized(this) {
      if (screenshotManager.isCapturingScreenshot) {
        return
      }
      screenshotManager.setCapturingState(true)
    }

    scope.launch(Dispatchers.IO) {
      var wasViewAttached = false
      var currentX = 0
      var currentY = 0

      try {
        withContext(Dispatchers.Main) {
          if (overlayView.parent != null) {
            wasViewAttached = true
            val layoutParams = overlayView.layoutParams as WindowManager.LayoutParams
            currentX = layoutParams.x
            currentY = layoutParams.y

            overlayView.alpha = 0f
            overlayView.isEnabled = false
            overlayView.cancelPendingInputEvents()
            overlayView.clearAnimation()
          } else {
            val fabSize = windowManager.getFabSizePx()
            currentX = windowManager.screenWidth - fabSize - windowManager.getEdgePaddingPx()
            currentY = windowManager.screenHeight / 2 - fabSize / 2
          }
        }

        delay(350)

        withContext(Dispatchers.Main) {
          if (wasViewAttached && overlayView.parent != null) {
            windowManager.removeView(overlayView)
          }
        }

        delay(SCREENSHOT_DELAY_MS)
        
        if (deferredSetupPerformed) {
          delay(500)
        }

        var retryCount = 0
        while (!screenshotManager.isVirtualDisplayReady() && retryCount < 15) {
          delay(100)
          retryCount++
        }

        var bitmap: Bitmap? = null
        var captureAttempt = 0
        val maxCaptureAttempts = 5
        
        while (bitmap == null && captureAttempt < maxCaptureAttempts) {
          if (captureAttempt > 0) {
            delay(500)
          }
          
          bitmap = screenshotManager.captureScreenBitmap(
            windowManager.screenWidth,
            windowManager.screenHeight,
            validateContent = true
          )
          captureAttempt++
        }

        if (bitmap != null) {
          val screenshotFile = screenshotManager.saveBitmapAndNotify(bitmap)

          if (screenshotFile != null) {
            delay(SCREENSHOT_DELAY_MS)
            
            withContext(Dispatchers.Main) {
              reattachOverlay(currentX, currentY)
              returnToAptoideGames()
            }
          } else {
            withContext(Dispatchers.Main) {
              reattachOverlay(currentX, currentY)
            }
          }
        } else {
          withContext(Dispatchers.Main) {
            screenshotManager.cleanup()
            hasScreenshotPermission = false
            pendingMediaProjectionResultCode = 0
            pendingMediaProjectionData = null
            
            reattachOverlay(currentX, currentY)
            returnToAptoideGamesForPermission()
          }
        }
      } catch (e: Exception) {
        e.printStackTrace()
        withContext(Dispatchers.Main) {
          reattachOverlay(currentX, currentY)
        }
      } finally {
        screenshotManager.setCapturingState(false)
      }
    }
  }

  private fun reattachOverlay(
    x: Int,
    y: Int,
  ) {
    if (overlayView.parent == null) {
      val fabSize = windowManager.getFabSizePx()
      val params = WindowManager.LayoutParams(
        fabSize,
        fabSize,
        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
          WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
          WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
        android.graphics.PixelFormat.TRANSLUCENT
      ).apply {
        gravity = android.view.Gravity.TOP or android.view.Gravity.START
        this.x = x
        this.y = y
      }
      overlayView.clearAnimation()
      overlayView.alpha = 1f
      overlayView.isEnabled = true
      windowManager.addView(overlayView, params)
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    isServiceRunning = false
    hasScreenshotPermission = false

    try {
      displayMonitor.stop()
      screenshotManager.cleanup()
      lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
      myViewModelStore.clear()
      scope.cancel()
      windowManager.removeView(overlayView)
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  private fun startAsForegroundService() {
    val channel = NotificationChannel(
      CHANNEL_ID,
      CHANNEL_NAME,
      NotificationManager.IMPORTANCE_LOW
    )
    val notificationManager = getSystemService(NotificationManager::class.java)
    notificationManager.createNotificationChannel(channel)

    val notification = NotificationCompat.Builder(this, CHANNEL_ID)
      .setContentTitle(getString(R.string.gamegenie_overlay_notification_title))
      .setContentText(getString(R.string.gamegenie_overlay_notification_text))
      .setSmallIcon(R.drawable.app_icon)
      .setOngoing(true)
      .build()

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      startForeground(
        NOTIFICATION_ID,
        notification,
        android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION
      )
    } else {
      startForeground(NOTIFICATION_ID, notification)
    }
  }
}
