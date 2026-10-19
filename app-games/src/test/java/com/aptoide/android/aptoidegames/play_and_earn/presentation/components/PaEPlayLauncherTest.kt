package com.aptoide.android.aptoidegames.play_and_earn.presentation.components

import cm.aptoide.pt.test.gherkin.scenario
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

// AND-901: the Play tap picks the service mode per game, from what the campaign needs and what the
// device currently allows. It never asks for permissions itself.
internal class PaEPlayLauncherTest {

  private class Starts {
    var tracking = 0
    var status: String? = null
  }

  private fun launcher(
    needsUsageAccess: Boolean?,
    usageTrackingEnabled: Boolean = true,
    optedIn: Boolean = true,
    hasUsagePermissions: () -> Boolean = { true },
    starts: Starts = Starts(),
  ) = PaEPlayLauncher(
    needsUsageAccess = needsUsageAccess,
    usageTrackingEnabled = usageTrackingEnabled,
    optedIn = optedIn,
    hasUsagePermissions = hasUsagePermissions,
    startTracking = { starts.tracking++ },
    startStatus = { starts.status = it },
  )

  @Test
  fun `Time or mixed game with the permissions and the flag on runs in tracking mode`() = scenario {
    m Given "a game that requires usage access, permissions granted, usage tracking enabled"
    val launcher = launcher(needsUsageAccess = true)

    m When "the mode is picked"
    val mode = launcher.mode()

    m Then "it is tracking"
    assertEquals(PaEPlayMode.TRACKING, mode)
  }

  @Test
  fun `Event-only game runs in status mode even with the permissions`() = scenario {
    m Given "a game that does not require usage access, permissions granted, flag on"
    val launcher = launcher(needsUsageAccess = false)

    m When "the mode is picked"
    val mode = launcher.mode()

    m Then "it is status"
    assertEquals(PaEPlayMode.STATUS, mode)
  }

  @Test
  fun `Time game without the permissions runs in status mode`() = scenario {
    m Given "a game that requires usage access, permissions missing, flag on"
    val launcher = launcher(needsUsageAccess = true, hasUsagePermissions = { false })

    m When "the mode is picked"
    val mode = launcher.mode()

    m Then "it is status: Play never asks for permissions"
    assertEquals(PaEPlayMode.STATUS, mode)
  }

  @Test
  fun `Flag off runs every game in status mode`() = scenario {
    m Given "a game that requires usage access, permissions granted, usage tracking disabled"
    val launcher = launcher(needsUsageAccess = true, usageTrackingEnabled = false)

    m When "the mode is picked"
    val mode = launcher.mode()

    m Then "it is status (Phase 1 behaviour)"
    assertEquals(PaEPlayMode.STATUS, mode)
  }

  @Test
  fun `A user who opted out in Settings runs in status mode`() = scenario {
    m Given "a time game, permissions granted, flag on, usage tracking switched off in Settings"
    var reads = 0
    val launcher = launcher(
      needsUsageAccess = true,
      optedIn = false,
      hasUsagePermissions = { reads++; true },
    )

    m When "the mode is picked"
    val mode = launcher.mode()

    m Then "it is status and the permissions were not consulted (AND-902 opt-out)"
    assertEquals(PaEPlayMode.STATUS, mode)
    assertEquals(0, reads)
  }

  @Test
  fun `Unknown missions run in status mode`() = scenario {
    m Given "the game's missions could not be read, permissions granted, flag on"
    val launcher = launcher(needsUsageAccess = null)

    m When "the mode is picked"
    val mode = launcher.mode()

    m Then "it is status"
    assertEquals(PaEPlayMode.STATUS, mode)
  }

  @Test
  fun `Permissions are read when the mode is picked, not when the launcher is built`() = scenario {
    m Given "permissions that get granted after the launcher was built"
    var granted = false
    val launcher = launcher(needsUsageAccess = true, hasUsagePermissions = { granted })
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
    val launcher = launcher(needsUsageAccess = false, hasUsagePermissions = { reads++; true })

    m When "the mode is picked"
    launcher.mode()

    m Then "the permissions were not consulted"
    assertEquals(0, reads)
  }

  @Test
  fun `Tracking mode starts the tracking service only`() = scenario {
    m Given "a game that runs in tracking mode"
    val starts = Starts()
    val launcher = launcher(needsUsageAccess = true, starts = starts)

    m When "it is launched"
    launcher.launch("com.game.time")

    m Then "the tracking service starts and no status heartbeat does"
    assertEquals(1, starts.tracking)
    assertNull(starts.status)
  }

  @Test
  fun `Status mode starts the status heartbeat for the game only`() = scenario {
    m Given "a game that runs in status mode"
    val starts = Starts()
    val launcher = launcher(needsUsageAccess = false, starts = starts)

    m When "it is launched"
    launcher.launch("com.game.event")

    m Then "the status heartbeat starts for that game and the tracking service does not"
    assertEquals("com.game.event", starts.status)
    assertEquals(0, starts.tracking)
  }

  @Test
  fun `Tracking mode needs every condition at once`() = scenario {
    m Given "every combination of flag, opt-in, missions (yes / no / unknown) and permissions"
    val combinations = buildList {
      for (flag in listOf(true, false)) for (optedIn in listOf(true, false)) {
        for (needs in listOf(true, false, null)) for (perms in listOf(true, false)) {
          add(listOf(flag, optedIn, needs, perms))
        }
      }
    }

    m When "the mode is picked for each one"
    val modes = combinations.associateWith { (flag, optedIn, needs, perms) ->
      launcher(
        needsUsageAccess = needs,
        usageTrackingEnabled = flag!!,
        optedIn = optedIn!!,
        hasUsagePermissions = { perms!! },
      ).mode()
    }

    m Then "only the all-true row tracks; the other 23 run in status mode"
    val expected = combinations.associateWith { (flag, optedIn, needs, perms) ->
      if (flag == true && optedIn == true && needs == true && perms == true) {
        PaEPlayMode.TRACKING
      } else {
        PaEPlayMode.STATUS
      }
    }
    assertEquals(expected, modes)
    assertEquals(1, modes.values.count { it == PaEPlayMode.TRACKING })
  }
}
