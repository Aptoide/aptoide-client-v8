package com.aptoide.android.aptoidegames.play_and_earn.presentation.overlays

import android.content.Context
import android.content.Context.WINDOW_SERVICE
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import cm.aptoide.pt.campaigns.domain.PaEMission
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaEOverlayViewManager @Inject constructor(
  @ApplicationContext private val context: Context,
) {

  suspend fun showWelcomeBackOverlayView(
    lifecycleOwner: LifecycleOwner,
    savedStateRegistryOwner: SavedStateRegistryOwner
  ) {
    val view = getWelcomeBackView(context)

    view.apply {
      setViewTreeLifecycleOwner(lifecycleOwner)
      setViewTreeSavedStateRegistryOwner(savedStateRegistryOwner)
    }

    val windowManager = context.getSystemService(WINDOW_SERVICE) as WindowManager
    val layoutParams = WindowManager.LayoutParams(
      WindowManager.LayoutParams.WRAP_CONTENT,
      WindowManager.LayoutParams.WRAP_CONTENT,
      WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
      WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
      PixelFormat.TRANSLUCENT
    ).apply {
      gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
      y = 100
    }

    windowManager.addView(view, layoutParams)

    delay(5000L)

    windowManager.removeView(view)
  }

  suspend fun showMissionCompletedOverlayView(
    mission: PaEMission,
    lifecycleOwner: LifecycleOwner,
    savedStateRegistryOwner: SavedStateRegistryOwner
  ) {
    val view = getMissionCompletedOverlayView(context, mission)

    view.apply {
      setViewTreeLifecycleOwner(lifecycleOwner)
      setViewTreeSavedStateRegistryOwner(savedStateRegistryOwner)
    }

    val windowManager = context.getSystemService(WINDOW_SERVICE) as WindowManager
    val layoutParams = WindowManager.LayoutParams(
      WindowManager.LayoutParams.WRAP_CONTENT,
      WindowManager.LayoutParams.WRAP_CONTENT,
      WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
      WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
      PixelFormat.TRANSLUCENT
    ).apply {
      gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
      y = 100
    }

    windowManager.addView(view, layoutParams)

    delay(5000L)

    windowManager.removeView(view)
  }
}

fun getWelcomeBackView(context: Context): View = ComposeView(context).apply {
  setContent {
    WelcomeBackOverlayView()
  }
}

fun getMissionCompletedOverlayView(context: Context, mission: PaEMission): View =
  ComposeView(context).apply {
    setContent {
      MissionCompletedOverlayView(mission)
    }
  }
