package com.aptoide.android.aptoidegames.ads.mrec

/** What the MREC view should do with its auto-refresh timer. */
enum class MrecRefreshAction { START, STOP, NONE }

/**
 * Pure rules for the MREC auto-refresh timer, kept out of the Android view so they are testable.
 *
 * The timer runs only for a loaded ad whose slot is active and only when auto-refresh was
 * switched on in Remote Config. Anything else stops it. Because the MAX SDK can ignore a
 * `stopAutoRefresh()` issued before the first ad loaded, the view also sets
 * `allow_pause_auto_refresh_immediately` and re-applies the stop after every load.
 */
class MrecRefreshPolicy(private val autoRefresh: Boolean) {

  /**
   * After an ad loaded. The view stops the timer explicitly before its first load, and MAX does
   * not restart a stopped timer by itself, so with auto-refresh on and an active slot the timer
   * must be started here; otherwise it stays (or is put) stopped.
   */
  fun onLoaded(active: Boolean): MrecRefreshAction = when {
    !autoRefresh -> MrecRefreshAction.STOP
    active -> MrecRefreshAction.START
    else -> MrecRefreshAction.STOP
  }

  /** The slot became active or inactive (screen resumed / paused, slot visible / covered). */
  fun onActiveChanged(active: Boolean, loaded: Boolean): MrecRefreshAction = when {
    !active -> MrecRefreshAction.STOP
    autoRefresh && loaded -> MrecRefreshAction.START
    else -> MrecRefreshAction.NONE
  }

  companion object {
    /** SDK extra parameter that makes stopAutoRefresh() take effect immediately. */
    const val ALLOW_PAUSE_PARAMETER = "allow_pause_auto_refresh_immediately"
  }
}
