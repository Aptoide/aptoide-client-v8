package com.aptoide.android.aptoidegames.play_and_earn.presentation.notifications

import cm.aptoide.pt.campaigns.domain.PaEMission
import cm.aptoide.pt.campaigns.domain.PaEMissionType
import cm.aptoide.pt.test.gherkin.coScenario
import com.aptoide.android.aptoidegames.play_and_earn.domain.sessions.CompletedMissionEvent
import com.google.gson.JsonObject
import kotlinx.coroutines.CancellationException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

// PO rule (2026-10-22): a completed mission shows the overlay when the app may draw over the
// game, the notification otherwise. Nothing else decides it.
internal class PaEMissionCompletedAnnouncerTest {

  private fun mission(title: String) = PaEMission(
    title = title,
    description = null,
    icon = null,
    type = PaEMissionType.PLAY_TIME,
    arguments = JsonObject(),
    units = 5,
    progress = null,
  )

  private fun event(title: String = "Play 30 seconds", next: String? = null) =
    CompletedMissionEvent(
      mission = mission(title),
      packageName = "com.game",
      nextMission = next?.let(::mission),
    )

  /** Records every call; a failure, when set, is thrown on the first call only. */
  private class Surfaces(
    private var overlayFailure: Exception? = null,
    private var notificationFailure: Exception? = null,
  ) {
    val overlays = mutableListOf<CompletedMissionEvent>()
    val notifications = mutableListOf<CompletedMissionEvent>()

    suspend fun overlay(event: CompletedMissionEvent) {
      overlays += event
      overlayFailure?.let { overlayFailure = null; throw it }
    }

    suspend fun notification(event: CompletedMissionEvent) {
      notifications += event
      notificationFailure?.let { notificationFailure = null; throw it }
    }
  }

  private fun announcer(surfaces: Surfaces, hasOverlayPermission: () -> Boolean) =
    PaEMissionCompletedAnnouncer(
      hasOverlayPermission = hasOverlayPermission,
      showOverlay = surfaces::overlay,
      showNotification = surfaces::notification,
    )

  @Test
  fun `With the overlay permission the mission shows as an overlay`() = coScenario {
    m Given "an app allowed to draw over other apps"
    val surfaces = Surfaces()

    m When "a mission completes"
    val surface = announcer(surfaces) { true }.announce(event())

    m Then "the overlay is shown and no notification"
    assertEquals(PaEMissionCompletedSurface.OVERLAY, surface)
    assertEquals(listOf(event()), surfaces.overlays)
    assertEquals(emptyList<CompletedMissionEvent>(), surfaces.notifications)
  }

  @Test
  fun `Without the overlay permission the mission shows as a notification`() = coScenario {
    m Given "an app not allowed to draw over other apps"
    val surfaces = Surfaces()

    m When "a mission completes"
    val surface = announcer(surfaces) { false }.announce(event())

    m Then "the notification is shown and no overlay"
    assertEquals(PaEMissionCompletedSurface.NOTIFICATION, surface)
    assertEquals(listOf(event()), surfaces.notifications)
    assertEquals(emptyList<CompletedMissionEvent>(), surfaces.overlays)
  }

  @Test
  fun `The game's next mission reaches the overlay with the completed one`() = coScenario {
    m Given "an app allowed to draw over other apps"
    val surfaces = Surfaces()

    m When "a mission completes in a game with another mission left"
    announcer(surfaces) { true }.announce(event(next = "Play 10 minutes"))

    m Then "the overlay gets the next mission to show"
    assertEquals("Play 10 minutes", surfaces.overlays.single().nextMission?.title)
  }

  @Test
  fun `The game's next mission reaches the notification too`() = coScenario {
    m Given "an app not allowed to draw over other apps"
    val surfaces = Surfaces()

    m When "a mission completes in a game with another mission left"
    announcer(surfaces) { false }.announce(event(next = "Play 10 minutes"))

    m Then "the notification gets the next mission to show"
    assertEquals("Play 10 minutes", surfaces.notifications.single().nextMission?.title)
  }

  @Test
  fun `The last mission of a game is announced without a next one`() = coScenario {
    m Given "an app allowed to draw over other apps"
    val surfaces = Surfaces()

    m When "the game's last open mission completes"
    announcer(surfaces) { true }.announce(event())

    m Then "the overlay has no next mission and looks as it always did"
    assertNull(surfaces.overlays.single().nextMission)
  }

  @Test
  fun `The permission is read for every mission, it can be revoked meanwhile`() = coScenario {
    m Given "the permission granted, then revoked while the service runs"
    val surfaces = Surfaces()
    var granted = true
    val announcer = announcer(surfaces) { granted }

    m When "a mission completes before and another after the change"
    val first = announcer.announce(event("first"))
    granted = false
    val second = announcer.announce(event("second"))

    m Then "each mission took the surface of its moment"
    assertEquals(PaEMissionCompletedSurface.OVERLAY, first)
    assertEquals(PaEMissionCompletedSurface.NOTIFICATION, second)
    assertEquals(listOf(event("first")), surfaces.overlays)
    assertEquals(listOf(event("second")), surfaces.notifications)
  }

  @Test
  fun `A failing overlay is contained and not replaced by a notification`() = coScenario {
    m Given "an overlay that fails to show"
    val surfaces = Surfaces(overlayFailure = IllegalStateException("no window"))

    m When "a mission completes"
    val surface = announcer(surfaces) { true }.announce(event())

    m Then "the overlay was attempted, nothing escapes, and the user is not announced twice"
    assertEquals(PaEMissionCompletedSurface.OVERLAY, surface)
    assertEquals(listOf(event()), surfaces.overlays)
    assertTrue(surfaces.notifications.isEmpty())
  }

  @Test
  fun `A failing notification is contained too`() = coScenario {
    m Given "a notification that fails to post"
    val surfaces = Surfaces(notificationFailure = IllegalStateException("bad icon"))

    m When "a mission completes"
    val surface = announcer(surfaces) { false }.announce(event())

    m Then "the notification was attempted and nothing escapes"
    assertEquals(PaEMissionCompletedSurface.NOTIFICATION, surface)
    assertEquals(listOf(event()), surfaces.notifications)
    assertTrue(surfaces.overlays.isEmpty())
  }

  @Test
  fun `After a failure the next mission is announced as usual`() = coScenario {
    m Given "an overlay that fails once"
    val surfaces = Surfaces(overlayFailure = IllegalStateException("no window"))
    val announcer = announcer(surfaces) { true }

    m When "two missions complete"
    announcer.announce(event("first"))
    announcer.announce(event("second"))

    m Then "both were attempted: the announcer carries no state between missions"
    assertEquals(listOf(event("first"), event("second")), surfaces.overlays)
  }

  @Test
  fun `Cancellation is not contained, so the collector stops with the service`() = coScenario {
    m Given "an overlay cancelled while it shows, the service stopping"
    val surfaces = Surfaces(overlayFailure = CancellationException("service stopped"))

    m When "a mission completes"
    val thrown = runCatching { announcer(surfaces) { true }.announce(event()) }.exceptionOrNull()

    m Then "the cancellation propagates and no notification is posted instead"
    assertTrue(thrown is CancellationException)
    assertTrue(surfaces.notifications.isEmpty())
  }
}
