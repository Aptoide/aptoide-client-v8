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
import com.aptoide.android.aptoidegames.play_and_earn.presentation.permissions.hasOverlayPermission
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaEOverlayViewManager @Inject constructor(
  @ApplicationContext private val context: Context,
) {

  suspend fun showWelcomeBackOverlayView(
    lifecycleOwner: LifecycleOwner,
    savedStateRegistryOwner: SavedStateRegistryOwner
  ) = show(getWelcomeBackView(context), lifecycleOwner, savedStateRegistryOwner)

  /** The mission-completed card over the game, for players who allowed drawing over apps. */
  suspend fun showMissionCompletedOverlayView(
    mission: PaEMission,
    lifecycleOwner: LifecycleOwner,
    savedStateRegistryOwner: SavedStateRegistryOwner
  ) = show(
    getMissionCompletedOverlayView(context, mission),
    lifecycleOwner,
    savedStateRegistryOwner
  )

  /**
   * Draws the view on top of everything for a few seconds. Needs `SYSTEM_ALERT_WINDOW`, which the
   * user can revoke while the service runs: then there is nothing to draw, and a window the
   * system refuses is logged, never a crash of the tracking service.
   */
  private suspend fun show(
    view: View,
    lifecycleOwner: LifecycleOwner,
    savedStateRegistryOwner: SavedStateRegistryOwner
  ) {
    if (!context.hasOverlayPermission()) {
      Timber.w("PaEOverlayViewManager: overlay permission gone, not showing")
      return
    }
    view.apply {
      setViewTreeLifecycleOwner(lifecycleOwner)
      setViewTreeSavedStateRegistryOwner(savedStateRegistryOwner)
    }

    val windowManager = context.getSystemService(WINDOW_SERVICE) as WindowManager
    val layoutParams = WindowManager.LayoutParams(
      WindowManager.LayoutParams.WRAP_CONTENT,
      WindowManager.LayoutParams.WRAP_CONTENT,
      WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
      WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
      PixelFormat.TRANSLUCENT
    ).apply {
      gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
      y = 100
    }

    try {
      windowManager.addView(view, layoutParams)
    } catch (e: RuntimeException) {
      // BadTokenException and friends: the permission was pulled between the check and here.
      Timber.w(e, "PaEOverlayViewManager: could not add the overlay")
      return
    }
    try {
      delay(OVERLAY_MILLIS)
    } finally {
      // A cancelled scope (the service stopping) must not leave the card on screen. The system
      // may already have torn the window down (permission revoked): not a reason to fail here.
      runCatching { windowManager.removeView(view) }
    }
  }

  private companion object {
    const val OVERLAY_MILLIS = 5000L
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
