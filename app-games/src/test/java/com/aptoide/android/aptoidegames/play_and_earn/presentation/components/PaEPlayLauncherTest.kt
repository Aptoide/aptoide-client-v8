package com.aptoide.android.aptoidegames.play_and_earn.presentation.components

import cm.aptoide.pt.test.gherkin.scenario
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

// AND-901: the Play tap picks the service mode per game, from what the campaign needs and what the
// device currently allows. It never asks for permissions itself.
internal class PaEPlayLauncherTest {

  @Test
  fun `Time or mixed game with the permissions and the flag on runs in tracking mode`() = scenario {
    m Given "a game that requires usage access, permissions granted, usage tracking enabled"
    val launcher = PaEPlayLauncher(
      needsUsageAccess = true,
      usageTrackingEnabled = true,
      hasUsagePermissions = { true },
    )

    m When "the mode is picked"
    val mode = launcher.mode()

    m Then "it is tracking"
    assertEquals(PaEPlayMode.TRACKING, mode)
  }

  @Test
  fun `Event-only game runs in status mode even with the permissions`() = scenario {
    m Given "a game that does not require usage access, permissions granted, flag on"
    val launcher = PaEPlayLauncher(
      needsUsageAccess = false,
      usageTrackingEnabled = true,
      hasUsagePermissions = { true },
    )

    m When "the mode is picked"
    val mode = launcher.mode()

    m Then "it is status"
    assertEquals(PaEPlayMode.STATUS, mode)
  }

  @Test
  fun `Time game without the permissions runs in status mode`() = scenario {
    m Given "a game that requires usage access, permissions missing, flag on"
    val launcher = PaEPlayLauncher(
      needsUsageAccess = true,
      usageTrackingEnabled = true,
      hasUsagePermissions = { false },
    )

    m When "the mode is picked"
    val mode = launcher.mode()

    m Then "it is status: Play never asks for permissions"
    assertEquals(PaEPlayMode.STATUS, mode)
  }

  @Test
  fun `Flag off runs every game in status mode`() = scenario {
    m Given "a game that requires usage access, permissions granted, usage tracking disabled"
    val launcher = PaEPlayLauncher(
      needsUsageAccess = true,
      usageTrackingEnabled = false,
      hasUsagePermissions = { true },
    )

    m When "the mode is picked"
    val mode = launcher.mode()

    m Then "it is status (Phase 1 behaviour)"
    assertEquals(PaEPlayMode.STATUS, mode)
  }

  @Test
  fun `Unknown missions run in status mode`() = scenario {
    m Given "the game's missions could not be read, permissions granted, flag on"
    val launcher = PaEPlayLauncher(
      needsUsageAccess = null,
      usageTrackingEnabled = true,
      hasUsagePermissions = { true },
    )

    m When "the mode is picked"
    val mode = launcher.mode()

    m Then "it is status"
    assertEquals(PaEPlayMode.STATUS, mode)
  }

  @Test
  fun `Permissions are read when the mode is picked, not when the launcher is built`() = scenario {
    m Given "permissions that get granted after the launcher was built"
    var granted = false
    val launcher = PaEPlayLauncher(
      needsUsageAccess = true,
      usageTrackingEnabled = true,
      hasUsagePermissions = { granted },
    )
    granted = true

    m When "the mode is picked"
    val mode = launcher.mode()

    m Then "the current permission state counts"
    assertEquals(PaEPlayMode.TRACKING, mode)
  }

  @Test
  fun `Permissions are not read when the game does not need them`() = scenario {
    m Given "an event-only game and a permission check that records calls"
    var reads = 0
    val launcher = PaEPlayLauncher(
      needsUsageAccess = false,
      usageTrackingEnabled = true,
      hasUsagePermissions = { reads++; true },
    )

    m When "the mode is picked"
    launcher.mode()

    m Then "the permissions were not consulted"
    assertEquals(0, reads)
  }
}
