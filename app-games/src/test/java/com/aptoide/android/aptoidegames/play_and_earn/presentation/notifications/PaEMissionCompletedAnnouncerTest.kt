package com.aptoide.android.aptoidegames.play_and_earn.presentation.notifications

import cm.aptoide.pt.campaigns.domain.PaEMission
import cm.aptoide.pt.campaigns.domain.PaEMissionType
import cm.aptoide.pt.test.gherkin.coScenario
import com.aptoide.android.aptoidegames.play_and_earn.domain.sessions.CompletedMissionEvent
import com.google.gson.JsonObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

// PO rule (2026-10-22): a completed mission shows the overlay when the app may draw over the
// game, the notification otherwise. Nothing else decides it.
internal class PaEMissionCompletedAnnouncerTest {

  private fun event(title: String = "Play 30 seconds") = CompletedMissionEvent(
    mission = PaEMission(
      title = title,
      description = null,
      icon = null,
      type = PaEMissionType.PLAY_TIME,
      arguments = JsonObject(),
      units = 5,
      progress = null,
    ),
    packageName = "com.game",
  )

  private class Surfaces(
    private val overlayFailure: Exception? = null,
    private val notificationFailure: Exception? = null,
  ) {
    val overlays = mutableListOf<CompletedMissionEvent>()
    val notifications = mutableListOf<CompletedMissionEvent>()

    suspend fun overlay(event: CompletedMissionEvent) {
      overlays += event
      overlayFailure?.let { throw it }
    }

    suspend fun notification(event: CompletedMissionEvent) {
      notifications += event
      notificationFailure?.let { throw it }
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

    m Then "nothing escapes to the collector, and the user is not announced twice"
    assertEquals(PaEMissionCompletedSurface.OVERLAY, surface)
    assertEquals(emptyList<CompletedMissionEvent>(), surfaces.notifications)
  }

  @Test
  fun `A failing notification is contained too`() = coScenario {
    m Given "a notification that fails to post"
    val surfaces = Surfaces(notificationFailure = IllegalStateException("bad icon"))

    m When "a mission completes"
    val surface = announcer(surfaces) { false }.announce(event())

    m Then "nothing escapes to the collector"
    assertEquals(PaEMissionCompletedSurface.NOTIFICATION, surface)
  }
}
