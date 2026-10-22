package com.aptoide.android.aptoidegames.play_and_earn.presentation.notifications

import com.aptoide.android.aptoidegames.play_and_earn.domain.sessions.CompletedMissionEvent
import kotlinx.coroutines.CancellationException
import timber.log.Timber

/** How a completed mission was announced to the player. */
enum class PaEMissionCompletedSurface { OVERLAY, NOTIFICATION }

/**
 * Announces a completed mission the way the PO decided (2026-10-22): the overlay on top of the
 * game when the app may draw over other apps, the notification (AND-880) otherwise. The
 * permission is read for every mission, since it can be revoked while the service runs.
 *
 * Never throws: the collector behind it has no replay and is not re-run, so one bad icon
 * download or window failure must not silence every mission after it. A failed overlay is not
 * replaced by a notification either; it is a bug to log, not a reason to announce twice.
 */
class PaEMissionCompletedAnnouncer(
  private val hasOverlayPermission: () -> Boolean,
  private val showOverlay: suspend (CompletedMissionEvent) -> Unit,
  private val showNotification: suspend (CompletedMissionEvent) -> Unit,
) {

  suspend fun announce(event: CompletedMissionEvent): PaEMissionCompletedSurface {
    val surface = if (hasOverlayPermission()) {
      PaEMissionCompletedSurface.OVERLAY
    } else {
      PaEMissionCompletedSurface.NOTIFICATION
    }
    try {
      when (surface) {
        PaEMissionCompletedSurface.OVERLAY -> showOverlay(event)
        PaEMissionCompletedSurface.NOTIFICATION -> showNotification(event)
      }
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      Timber.e(e, "PaEForegroundService: mission $surface failed for ${event.packageName}")
    }
    return surface
  }
}
