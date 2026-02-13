package com.aptoide.android.aptoidegames.gamegenie.presentation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.gamegenie.analytics.GameGenieAnalytics
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    const val EXTRA_TARGET_PACKAGE = "TARGET_PACKAGE"
    const val EXTRA_COMPANION_PACKAGE = "COMPANION_PACKAGE"

    private const val ANIMATION_DURATION_MS = 300L
    private const val ANIMATION_FRAME_DELAY_MS = 16L

    private const val SCREENSHOT_DELAY_MS = 100L

    @Volatile
    var isServiceRunning: Boolean = false
      private set

    private val _overlayRunningState = MutableStateFlow(false)
    val overlayRunningState: StateFlow<Boolean> = _overlayRunningState.asStateFlow()
    
    private val _captureReadyState = MutableStateFlow(false)
    val captureReadyState: StateFlow<Boolean> = _captureReadyState.asStateFlow()

    @Volatile
    var hasScreenshotPermission: Boolean = false
      private set
    
    @Volatile
    var needsMediaProjectionPermission: Boolean = false
    
    fun clearPermissionRequest() {
      needsMediaProjectionPermission = false
    }

    fun resetCaptureReadiness() {
      _captureReadyState.value = false
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
  private var targetPackage by mutableStateOf<String?>(null)
  private var processLifecycleObserver: LifecycleEventObserver? = null

  override fun onBind(intent: Intent?): IBinder? = null

  override fun onCreate() {
    super.onCreate()
    isServiceRunning = true
    _overlayRunningState.value = true
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
      _captureReadyState.value = false
    }

    displayMonitor = OverlayDisplayMonitor(
      context = this,
      scope = scope,
      onDimensionChanged = { handleDimensionChange() }
    )
  }

  private fun startCaptureReadinessCheck() {
    scope.launch(Dispatchers.IO) {
      delay(2000)
      var attempts = 0
      val maxAttempts = 30 // 15 seconds total (30 * 500ms)

      while (!_captureReadyState.value && attempts < maxAttempts && isServiceRunning) {
        val testBitmap = screenshotManager.captureScreenBitmap(
          windowManager.screenWidth,
          windowManager.screenHeight,
          validateContent = true
        )

        if (testBitmap != null) {
          testBitmap.recycle()
          withContext(Dispatchers.Main) {
            _captureReadyState.value = true
          }
          break
        }

        attempts++
        delay(500)
      }
    }
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
      var isAptoideGamesInForeground by mutableStateOf(
        ProcessLifecycleOwner.get().lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)
      )

      onOrientationChanged = { newX, newY ->
        fabX = newX
        fabY = newY
      }

      processLifecycleObserver?.let { ProcessLifecycleOwner.get().lifecycle.removeObserver(it) }
      processLifecycleObserver = LifecycleEventObserver { _, event ->
        when (event) {
          Lifecycle.Event.ON_START,
          Lifecycle.Event.ON_RESUME -> {
            isAptoideGamesInForeground = true
          }
          Lifecycle.Event.ON_PAUSE,
          Lifecycle.Event.ON_STOP -> {
            isAptoideGamesInForeground = false
          }
          else -> Unit
        }
      }
      ProcessLifecycleOwner.get().lifecycle.addObserver(processLifecycleObserver!!)

      val onMenuToggle: () -> Unit = {
        val newMenuState = !showMenu

        if (newMenuState) {
          showMenu = true
          val layoutParams = overlayView.layoutParams as WindowManager.LayoutParams
          val actualX = layoutParams.x
          val actualY = layoutParams.y
          showMenuWindow(
            actualX,
            actualY,
            actualX < windowManager.screenWidth / 2,
            analytics,
            showOnlyRemove = isAptoideGamesInForeground
          )
        } else {
          showMenu = false
          hideMenuWindow()
        }
      }

      setContent {
        val fabSize = OverlayWindowManager.FAB_SIZE_DP.dp
        val fabSizePx = with(LocalDensity.current) { fabSize.toPx().toInt() }
        val targetAppIcon = rememberTargetAppIcon(targetPackage)
        
        val isCaptureReady by captureReadyState.collectAsState()

        AptoideTheme {
          GameGenieOverlay(
            showMenu = showMenu,
            isAptoideGamesInForeground = isAptoideGamesInForeground,
            targetAppIcon = targetAppIcon,
            isCaptureReady = isCaptureReady,
            onMenuToggle = onMenuToggle,
            onDrag = { dx, dy ->
              fabX = (fabX + dx)
                .coerceIn(
                  windowManager.getEdgePaddingPx(),
                  windowManager.screenWidth - fabSizePx - windowManager.getEdgePaddingPx()
                )

              fabY = (fabY + dy)
                .coerceIn(
                  windowManager.getEdgePaddingPx(),
                  windowManager.screenHeight - fabSizePx - windowManager.getEdgePaddingPx()
                )

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
              if (isAptoideGamesInForeground()) {
                returnToTargetApp()
              } else {
                takeScreenshot()
              }
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

  override fun onConfigurationChanged(newConfig: Configuration) {
    super.onConfigurationChanged(newConfig)
    handleDimensionChange()
  }

  override fun onStartCommand(
    intent: Intent?,
    flags: Int,
    startId: Int,
  ): Int {
    super.onStartCommand(intent, flags, startId)

    intent?.getStringExtra(EXTRA_TARGET_PACKAGE)?.let { packageName ->
      targetPackage = packageName
    }

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
      _captureReadyState.value = false

      screenshotManager.setupMediaProjection(pendingMediaProjectionResultCode, pendingMediaProjectionData)
      screenshotManager.setupVirtualDisplay(
        windowManager.screenWidth,
        windowManager.screenHeight,
        onFirstFrameReady = {
          startCaptureReadinessCheck()
        }
      )
      pendingMediaProjectionResultCode = 0
      pendingMediaProjectionData = null
    } else if (screenshotManager.hasPermissionData()) {
      if (screenshotManager.needsRecreation()) {
        if (screenshotManager.hasMediaProjection()) {
          _captureReadyState.value = false
          screenshotManager.setupVirtualDisplay(
            windowManager.screenWidth,
            windowManager.screenHeight,
            onFirstFrameReady = {
              startCaptureReadinessCheck()
            }
          )
        }
      }
    }

    return START_NOT_STICKY
  }

  private fun showMenuWindow(
    fabX: Int,
    fabY: Int,
    isFabOnLeftSide: Boolean,
    analytics: GameGenieAnalytics,
    showOnlyRemove: Boolean,
  ) {
    hideMenuWindow()

    val menuView = ComposeView(this).apply {
      setViewTreeLifecycleOwner(this@GameGenieOverlayService)
      setViewTreeViewModelStoreOwner(this@GameGenieOverlayService)
      setViewTreeSavedStateRegistryOwner(this@GameGenieOverlayService)

      setContent {
        AptoideTheme {
          GameGenieMenu(
            onScreenshot = {
              analytics.sendGameGenieOverlayScreenshot()
              hideMenuWindow()
              takeScreenshot()
            },
            onCloseOverlay = {
              analytics.sendGameGenieOverlayRemove()
              hideMenuWindow()
              stopService(Intent(this@GameGenieOverlayService, GameGenieOverlayService::class.java))
            },
            showOnlyRemove = showOnlyRemove
          )
        }
      }
    }

    val fabSizePx = windowManager.getFabSizePx()
    val spacingPx = windowManager.getMenuSpacingPx()
    val menuHeightPx = if (showOnlyRemove) {
      windowManager.getRemoveMenuHeightPx()
    } else {
      windowManager.getMenuHeightPx()
    }
    val edgePaddingPx = windowManager.getEdgePaddingPx()

    val menuX = if (isFabOnLeftSide) {
      fabX
    } else {
      fabX - windowManager.getMenuLeftExtensionPx()
    }
    val preferredBelowY = fabY + fabSizePx + spacingPx
    val preferredAboveY = fabY - spacingPx - menuHeightPx
    val hasSpaceBelow = preferredBelowY + menuHeightPx <= windowManager.screenHeight - edgePaddingPx
    val rawMenuY = if (hasSpaceBelow) preferredBelowY else preferredAboveY
    val menuY = rawMenuY.coerceIn(
      edgePaddingPx,
      windowManager.screenHeight - menuHeightPx - edgePaddingPx
    )

    val menuParams = WindowManager.LayoutParams(
      windowManager.getMenuWidthPx(),
      menuHeightPx,
      WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
      WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
      PixelFormat.TRANSLUCENT
    ).apply {
      gravity = Gravity.TOP or Gravity.START
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
      if (!isAptoideGamesInForeground()) {
        targetPackage?.let { putExtra(EXTRA_COMPANION_PACKAGE, it) }
      }
      startActivity(this)
    }
  }

  private fun returnToTargetApp() {
    val target = targetPackage ?: return
    val intent = packageManager.getLaunchIntentForPackage(target)
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
      if (!isAptoideGamesInForeground()) {
        targetPackage?.let { putExtra(EXTRA_COMPANION_PACKAGE, it) }
      }
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
        PixelFormat.TRANSLUCENT
      ).apply {
        gravity = Gravity.TOP or Gravity.START
        this.x = x
        this.y = y
      }
      overlayView.clearAnimation()
      overlayView.alpha = 1f
      overlayView.isEnabled = true
      windowManager.addView(overlayView, params)
    }
  }

  private fun isAptoideGamesInForeground(): Boolean {
    val state = ProcessLifecycleOwner.get().lifecycle.currentState
    return state.isAtLeast(Lifecycle.State.STARTED)
  }

  @Composable
  private fun rememberTargetAppIcon(packageName: String?): ImageBitmap? {
    val context = LocalContext.current
    var icon by remember(packageName) { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(packageName) {
      icon = if (packageName.isNullOrBlank()) {
        null
      } else {
        withContext(Dispatchers.IO) {
          runCatching {
            context.packageManager.getApplicationIcon(packageName)
              .toBitmap()
              .asImageBitmap()
          }.getOrNull()
        }
      }
    }

    return icon
  }

  override fun onTaskRemoved(rootIntent: Intent?) {
    super.onTaskRemoved(rootIntent)
    stopSelf()
  }

  override fun onDestroy() {
    super.onDestroy()
    isServiceRunning = false
    hasScreenshotPermission = false
    _overlayRunningState.value = false

    processLifecycleObserver?.let { ProcessLifecycleOwner.get().lifecycle.removeObserver(it) }
    processLifecycleObserver = null

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
        ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION
      )
    } else {
      startForeground(NOTIFICATION_ID, notification)
    }
  }
}
