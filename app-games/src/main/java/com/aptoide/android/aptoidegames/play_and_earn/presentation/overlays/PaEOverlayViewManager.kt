package com.aptoide.android.aptoidegames.play_and_earn.presentation.overlays

import android.content.Context
import android.content.Context.WINDOW_SERVICE
import android.graphics.PixelFormat
import android.hardware.input.InputManager
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import cm.aptoide.pt.campaigns.domain.PaEMission
import cm.aptoide.pt.wallet.wallet_info.domain.GetWalletUnitsUseCase
import com.aptoide.android.aptoidegames.play_and_earn.presentation.permissions.hasOverlayPermission
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaEOverlayViewManager @Inject constructor(
  @ApplicationContext private val context: Context,
  private val getWalletUnitsUseCase: GetWalletUnitsUseCase,
) {

  suspend fun showWelcomeBackOverlayView(
    lifecycleOwner: LifecycleOwner,
    savedStateRegistryOwner: SavedStateRegistryOwner
  ) = show(getWelcomeBackView(context), lifecycleOwner, savedStateRegistryOwner)

  /**
   * The mission-completed banner over the game, for players who allowed drawing over apps. It
   * stays until Claim is tapped, or claims by itself after [AUTO_CLAIM_MILLIS]; then the coins
   * fly to the balance, which counts up, and everything slides away (Hackathon 2026, T5).
   *
   * Two windows: the banner, as small as its content, takes touches for its Claim button; the
   * effects layer spans the screen's width for the balance and the flying coins, so it must let
   * every touch through to the game.
   */
  suspend fun showMissionCompletedOverlayView(
    mission: PaEMission,
    nextMission: PaEMission?,
    lifecycleOwner: LifecycleOwner,
    savedStateRegistryOwner: SavedStateRegistryOwner
  ) {
    if (!context.hasOverlayPermission()) {
      Timber.w("PaEOverlayViewManager: overlay permission gone, not showing")
      return
    }
    val state = MissionCompletedOverlayState(mission, nextMission)
    val effects = ComposeView(context).apply {
      setContent { MissionCompletedEffectsView(state, topInset = OVERLAY_TOP_PX) }
    }
    val banner = ComposeView(context).apply {
      setContent { MissionCompletedOverlayView(state) }
    }
    listOf(effects, banner).forEach {
      it.setViewTreeLifecycleOwner(lifecycleOwner)
      it.setViewTreeSavedStateRegistryOwner(savedStateRegistryOwner)
    }

    val windowManager = context.getSystemService(WINDOW_SERVICE) as WindowManager
    try {
      windowManager.addView(effects, effectsLayoutParams())
      windowManager.addView(banner, bannerLayoutParams())
    } catch (e: RuntimeException) {
      // BadTokenException and friends: the permission was pulled between the check and here.
      Timber.w(e, "PaEOverlayViewManager: could not add the mission overlay")
      runCatching { windowManager.removeView(effects) }
      return
    }
    try {
      coroutineScope {
        // The banner does not wait for the wallet: the balance joins it when it is known.
        val balance = launch {
          state.balanceAfter = try {
            withTimeoutOrNull(BALANCE_TIMEOUT_MILLIS) { getWalletUnitsUseCase() }
          } catch (e: CancellationException) {
            throw e
          } catch (e: Exception) {
            Timber.w(e, "PaEOverlayViewManager: could not read the units balance")
            null
          }
        }
        state.visible = true
        withTimeoutOrNull(AUTO_CLAIM_MILLIS) { state.awaitClaimTap() }
        // A balance arriving now would make the count jump: it counts what it knew at the claim.
        balance.cancel()
        state.phase = MissionClaimPhase.CLAIMED
        delay(COINS_FLIGHT_MILLIS.toLong())
        state.phase = MissionClaimPhase.COUNTING
        delay(BALANCE_COUNT_MILLIS + AFTER_CLAIM_MILLIS)
        state.visible = false
        delay(SLIDE_MILLIS.toLong())
      }
    } finally {
      // A cancelled scope (the service stopping) must not leave the banner on screen. The system
      // may already have torn the windows down (permission revoked): not a reason to fail here.
      runCatching { windowManager.removeView(banner) }
      runCatching { windowManager.removeView(effects) }
    }
  }

  private fun bannerLayoutParams() = WindowManager.LayoutParams(
    WindowManager.LayoutParams.WRAP_CONTENT,
    WindowManager.LayoutParams.WRAP_CONTENT,
    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
      WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
      WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
    PixelFormat.TRANSLUCENT
  ).apply {
    gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
    y = OVERLAY_TOP_PX
  }

  private fun effectsLayoutParams() = WindowManager.LayoutParams(
    WindowManager.LayoutParams.MATCH_PARENT,
    WindowManager.LayoutParams.WRAP_CONTENT,
    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
      WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
      WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
    PixelFormat.TRANSLUCENT
  ).apply {
    gravity = Gravity.TOP
    // Android 12+ blocks the touches that pass through another app's window unless that window
    // is see-through enough: at the system's limit, the game under the layer stays playable.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      alpha = context.getSystemService(InputManager::class.java).maximumObscuringOpacityForTouch
    }
  }

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
      y = OVERLAY_TOP_PX
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
    const val OVERLAY_TOP_PX = 100

    // Mission-completed banner: untouched, it claims by itself.
    const val AUTO_CLAIM_MILLIS = 10_000L
    const val AFTER_CLAIM_MILLIS = 500L
    const val BALANCE_TIMEOUT_MILLIS = 5_000L
  }
}

fun getWelcomeBackView(context: Context): View = ComposeView(context).apply {
  setContent {
    WelcomeBackOverlayView()
  }
}
