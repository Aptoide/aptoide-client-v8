package com.aptoide.android.aptoidegames.gamegenie.presentation.composables.companion

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.projection.MediaProjectionManager
import android.provider.Settings
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.core.content.ContextCompat.startForegroundService
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.gamegenie.domain.Suggestion
import com.aptoide.android.aptoidegames.gamegenie.domain.GameCompanion
import com.aptoide.android.aptoidegames.gamegenie.presentation.GameGenieOverlayService
import com.aptoide.android.aptoidegames.gamegenie.presentation.GameGenieUIState
import com.aptoide.android.aptoidegames.gamegenie.presentation.ScreenshotBroadcastReceiver
import com.aptoide.android.aptoidegames.gamegenie.presentation.TypingAnimation
import com.aptoide.android.aptoidegames.gamegenie.presentation.composables.ChatParticipantName
import com.aptoide.android.aptoidegames.gamegenie.presentation.composables.MessageList
import com.aptoide.android.aptoidegames.gamegenie.presentation.composables.PlayInGameOverlayTooltip
import com.aptoide.android.aptoidegames.gamegenie.presentation.composables.SelectedGameCompanion
import com.aptoide.android.aptoidegames.gamegenie.presentation.composables.TextInputBar
import com.aptoide.android.aptoidegames.gamegenie.presentation.composables.GameGenieOverlayPermissionSheet
import com.aptoide.android.aptoidegames.BottomSheetContent
import com.aptoide.android.aptoidegames.gamegenie.analytics.GameGenieAnalytics
import com.aptoide.android.aptoidegames.gamegenie.analytics.rememberGameGenieAnalytics
import com.aptoide.android.aptoidegames.gamegenie.data.GameGenieLocalRepository
import com.aptoide.android.aptoidegames.gamegenie.data.rememberGameGeniePreferences
import java.io.File

private suspend fun loadScreenshotIfValid(
  repository: GameGenieLocalRepository,
  onPathLoaded: (String?) -> Unit
) {
  val path = repository.getScreenshotPath()
  val timestamp = repository.getScreenshotTimestamp()
  val currentTime = System.currentTimeMillis()
  val ageMinutes = (currentTime - timestamp) / (1000 * 60)

  if (path != null && File(path).exists() && ageMinutes < 2) {
    onPathLoaded(path)
  } else {
    if (path != null) {
      repository.clearScreenshot()
    }
    onPathLoaded(null)
  }
}

fun launchOverlayAndGame(
  selectedGame: GameCompanion,
  context: Context,
  targetAppLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>,
  mediaProjectionResultCode: Int = 0,
  mediaProjectionData: Intent? = null,
  analytics: GameGenieAnalytics,
) {
  analytics.sendGameGenieTryLaunchOverlay(selectedGame.packageName)
  val overlayIntent = Intent(context, GameGenieOverlayService::class.java).apply {
    putExtra("TARGET_PACKAGE", selectedGame.packageName)
    if (mediaProjectionResultCode != 0 && mediaProjectionData != null) {
      putExtra("MEDIA_PROJECTION_RESULT_CODE", mediaProjectionResultCode)
      putExtra("MEDIA_PROJECTION_DATA", mediaProjectionData)
    }
  }
  startForegroundService(context, overlayIntent)

  val launchIntent = context.packageManager.getLaunchIntentForPackage(selectedGame.packageName)
  if (launchIntent != null) {
    targetAppLauncher.launch(launchIntent)
    analytics.sendGameGenieOverlayLaunched(selectedGame.packageName)
  }
}

@Composable
fun ChatScreenCompanion(
  selectedGame: GameCompanion,
  firstLoad: Boolean,
  navigateBack: () -> Unit,
  uiState: GameGenieUIState,
  navigateTo: (String) -> Unit,
  setFirstLoadDone: () -> Unit,
  onMessageSend: (String, String?) -> Unit,
  isLoading: Boolean = false,
  suggestions: List<Suggestion> = emptyList(),
  onSuggestionClick: (String, Int) -> Unit = { _, _ -> },
  showBottomSheet: ((BottomSheetContent?) -> Unit)? = null,
) {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()
  val analytics = rememberGameGenieAnalytics()
  val repository = rememberGameGeniePreferences()

  var screenshotPath by remember { mutableStateOf<String?>(null) }
  var hasClickedOverlayButton by remember { mutableStateOf(false) }

  val shouldShowTooltip = uiState.chat.conversation.size > 1
  val showTooltip = shouldShowTooltip && !hasClickedOverlayButton

  LaunchedEffect(Unit) {
    hasClickedOverlayButton = repository.hasClickedOverlayButton()
    loadScreenshotIfValid(repository) { path ->
      screenshotPath = path
    }
  }

  var shouldRequestMediaProjection by remember { mutableStateOf(false) }

  val lifecycleOwner = LocalLifecycleOwner.current
  DisposableEffect(lifecycleOwner) {
    val observer = LifecycleEventObserver { _, event ->
      if (event == Lifecycle.Event.ON_RESUME) {
        coroutineScope.launch {
          loadScreenshotIfValid(repository) { path ->
            screenshotPath = path
          }
        }
        if (GameGenieOverlayService.needsMediaProjectionPermission) {
          GameGenieOverlayService.clearPermissionRequest()
          shouldRequestMediaProjection = true
        }
      }
    }
    lifecycleOwner.lifecycle.addObserver(observer)
    onDispose {
      lifecycleOwner.lifecycle.removeObserver(observer)
    }
  }

  val broadcastReceiver = remember {
    ScreenshotBroadcastReceiver { path ->
      screenshotPath = path
    }
  }

  @SuppressLint("UnspecifiedRegisterReceiverFlag")
  DisposableEffect(Unit) {
    val filter = IntentFilter(ScreenshotBroadcastReceiver.ACTION_SCREENSHOT_CAPTURED)
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
      context.registerReceiver(
        broadcastReceiver,
        filter,
        Context.RECEIVER_NOT_EXPORTED
      )
    } else {
      context.registerReceiver(broadcastReceiver, filter)
    }
    onDispose {
      context.unregisterReceiver(broadcastReceiver)
    }
  }

  val showImage = uiState.chat.conversation.size < 2

  Box(
    modifier = Modifier.fillMaxSize()
  ) {
    Column(
      modifier = Modifier
        .padding(horizontal = 18.dp)
        .fillMaxSize()
    ) {
      val targetAppLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
      ) { /* Service continues running after returning from target app */ }

      val mediaProjectionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
      ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
          coroutineScope.launch {
            launchOverlayAndGame(
              selectedGame,
              context,
              targetAppLauncher,
              result.resultCode,
              result.data,
              analytics
            )
          }
        } else {
          coroutineScope.launch {
            launchOverlayAndGame(selectedGame, context, targetAppLauncher, analytics = analytics)
          }
        }
      }

      val overlayPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
      ) {
        if (Settings.canDrawOverlays(context)) {
          val mediaProjectionManager = context.getSystemService(Context.MEDIA_PROJECTION_SERVICE)
            as MediaProjectionManager
          val permissionIntent = mediaProjectionManager.createScreenCaptureIntent()
          mediaProjectionLauncher.launch(permissionIntent)
        }
      }

      val reRequestMediaProjectionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
      ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
          val overlayIntent = Intent(context, GameGenieOverlayService::class.java).apply {
            putExtra(GameGenieOverlayService.EXTRA_MEDIA_PROJECTION_RESULT_CODE, result.resultCode)
            putExtra(GameGenieOverlayService.EXTRA_MEDIA_PROJECTION_DATA, result.data)
          }
          context.startService(overlayIntent)

          val launchIntent = context.packageManager.getLaunchIntentForPackage(selectedGame.packageName)
          if (launchIntent != null) {
            targetAppLauncher.launch(launchIntent)
          }
        }
      }

      LaunchedEffect(shouldRequestMediaProjection) {
        if (shouldRequestMediaProjection) {
          shouldRequestMediaProjection = false
          val mediaProjectionManager = context.getSystemService(Context.MEDIA_PROJECTION_SERVICE)
            as MediaProjectionManager
          val permissionIntent = mediaProjectionManager.createScreenCaptureIntent()
          reRequestMediaProjectionLauncher.launch(permissionIntent)
        }
      }

      val onLaunchOverlay: (String) -> Unit = { packageName ->
        hasClickedOverlayButton = true
        coroutineScope.launch {
          repository.setClickedOverlayButton(true)
        }
        analytics.sendGameGenieTryLaunchOverlay(packageName)

        if (GameGenieOverlayService.isServiceRunning && GameGenieOverlayService.hasScreenshotPermission) {
          val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
          if (launchIntent != null) {
            targetAppLauncher.launch(launchIntent)
            analytics.sendGameGenieOverlayLaunched(packageName)
          }
        } else if (!Settings.canDrawOverlays(context)) {
          if (showBottomSheet != null) {
            showBottomSheet(
              GameGenieOverlayPermissionSheet(
                onAccept = {
                  analytics.sendGameGenieOverlayDialogLetsDoIt()
                  val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    "package:${context.packageName}".toUri()
                  )
                  overlayPermissionLauncher.launch(intent)
                }
              )
            )
          } else {
            analytics.sendGameGenieOverlayDialogLetsDoIt()
            val intent = Intent(
              Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
              "package:${context.packageName}".toUri()
            )
            overlayPermissionLauncher.launch(intent)
          }
        } else {
          val mediaProjectionManager = context.getSystemService(Context.MEDIA_PROJECTION_SERVICE)
            as MediaProjectionManager
          val permissionIntent = mediaProjectionManager.createScreenCaptureIntent()
          mediaProjectionLauncher.launch(permissionIntent)
        }
      }

      SelectedGameCompanion(selectedGame, showImage, navigateBack, onLaunchOverlay)

      MessageList(
        messages = uiState.chat.conversation,
        firstLoad = firstLoad,
        navigateTo = navigateTo,
        setFirstLoadDone = setFirstLoadDone,
        isCompanion = true,
        modifier = Modifier.weight(1f),
        gameName = selectedGame.name,
        suggestions = suggestions,
        onSuggestionClick = onSuggestionClick) {
        onLaunchOverlay(selectedGame.packageName)
      }

      if (isLoading) {
        Row(
          horizontalArrangement = Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(bottom = 8.dp)
        ) {
          ChatParticipantName(
            stringResource(R.string.genai_bottom_navigation_gamegenie_button)
          )
          TypingAnimation()
        }
      }

      TextInputBar(
        onMessageSent = { message, imagePath ->
          if (imagePath != null) {
            onMessageSend(message, imagePath)
            coroutineScope.launch {
              repository.clearScreenshot()
            }
            screenshotPath = null
          } else {
            onMessageSend(message, null)
          }
        },
        screenshotPath = screenshotPath,
        onClearScreenshot = {
          coroutineScope.launch {
            repository.clearScreenshot()
          }
          screenshotPath = null
        },
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 8.dp)
      )
    }

    if (showTooltip && !showImage) {
      Box(
        modifier = Modifier
          .align(Alignment.TopEnd)
          .padding(
            top = 52.dp, end = 0.dp
          )
          .offset(x = (-9).dp)
      ) {
        PlayInGameOverlayTooltip()
      }
    }
  }
}
