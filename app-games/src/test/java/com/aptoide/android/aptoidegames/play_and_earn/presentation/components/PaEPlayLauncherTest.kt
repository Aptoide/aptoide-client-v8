package com.aptoide.android.aptoidegames.play_and_earn.presentation.components

import cm.aptoide.pt.campaigns.domain.PaECampaignType
import cm.aptoide.pt.test.gherkin.scenario
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

// AND-901: Play follows the campaign type. Play-time needs the usage-access permissions (asked
// for on the spot), event never does; the kill switch turns everything into event behaviour.
internal class PaEPlayLauncherTest {

  private class Calls {
    var open = 0
    var tracking = 0
    var status: String? = null
    var permissions = 0
  }

  private fun launcher(
    campaignType: PaECampaignType?,
    usageTrackingEnabled: Boolean = true,
    optedIn: Boolean = true,
    hasUsagePermissions: () -> Boolean = { true },
    canRequestPermissions: Boolean = true,
  ) = PaEPlayLauncher(
    campaignType = campaignType,
    usageTrackingEnabled = usageTrackingEnabled,
    optedIn = optedIn,
    hasUsagePermissions = hasUsagePermissions,
    canRequestPermissions = canRequestPermissions,
  )

  private fun PaEPlayLauncher.launch(calls: Calls) = launch(
    packageName = "com.game",
    open = { calls.open++ },
    startTracking = { calls.tracking++ },
    startStatus = { calls.status = it },
    requestPermissions = { calls.permissions++ },
  )

  @Test
  fun `A play-time campaign with the permissions runs in tracking mode`() = scenario {
    m Given "a play-time campaign, the permissions granted, the kill switch on"
    val launcher = launcher(PaECampaignType.PLAY_TIME)

    m When "the mode is picked"
    val mode = launcher.mode()

    m Then "it is tracking"
    assertEquals(PaEPlayMode.TRACKING, mode)
  }

  @Test
  fun `A play-time campaign without the permissions asks for them`() = scenario {
    m Given "a play-time campaign, the permissions missing, a surface that can navigate"
    val launcher = launcher(PaECampaignType.PLAY_TIME, hasUsagePermissions = { false })

    m When "the mode is picked"
    val mode = launcher.mode()

    m Then "the permission screens are requested"
    assertEquals(PaEPlayMode.REQUEST_PERMISSIONS, mode)
  }

  @Test
  fun `A play-time campaign without the permissions on a surface that can't navigate just plays`() =
    scenario {
      m Given "a play-time campaign, the permissions missing, no way to show the screens"
      val launcher = launcher(
        PaECampaignType.PLAY_TIME,
        hasUsagePermissions = { false },
        canRequestPermissions = false,
      )

      m When "the mode is picked"
      val mode = launcher.mode()

      m Then "it is status: the game opens, nothing is asked"
      assertEquals(PaEPlayMode.STATUS, mode)
    }

  @Test
  fun `An event campaign runs in status mode whatever the permissions`() = scenario {
    m Given "an event campaign, with and without the permissions"
    val granted = launcher(PaECampaignType.EVENT)
    val missing = launcher(PaECampaignType.EVENT, hasUsagePermissions = { false })

    m When "the modes are picked"
    val modes = listOf(granted.mode(), missing.mode())

    m Then "both are status"
    assertEquals(listOf(PaEPlayMode.STATUS, PaEPlayMode.STATUS), modes)
  }

  @Test
  fun `The kill switch off runs every campaign in status mode`() = scenario {
    m Given "a play-time campaign without the permissions, the kill switch off"
    var reads = 0
    val launcher = launcher(
      PaECampaignType.PLAY_TIME,
      usageTrackingEnabled = false,
      hasUsagePermissions = { reads++; false },
    )

    m When "the mode is picked"
    val mode = launcher.mode()

    m Then "it is status and the permissions were not consulted (Phase 1 behaviour)"
    assertEquals(PaEPlayMode.STATUS, mode)
    assertEquals(0, reads)
  }

  @Test
  fun `A user who opted out in Settings runs in status mode and is never asked`() = scenario {
    m Given "a play-time campaign without the permissions, usage tracking switched off in Settings"
    var reads = 0
    val launcher = launcher(
      PaECampaignType.PLAY_TIME,
      optedIn = false,
      hasUsagePermissions = { reads++; false },
    )

    m When "the mode is picked"
    val mode = launcher.mode()

    m Then "it is status: no permission screens, no tracking, permissions not consulted (AND-902)"
    assertEquals(PaEPlayMode.STATUS, mode)
    assertEquals(0, reads)
  }

  @Test
  fun `An unknown campaign runs in status mode`() = scenario {
    m Given "missions that could not be read, the permissions missing"
    val launcher = launcher(campaignType = null, hasUsagePermissions = { false })

    m When "the mode is picked"
    val mode = launcher.mode()

    m Then "it is status: nothing is asked of the user"
    assertEquals(PaEPlayMode.STATUS, mode)
  }

  @Test
  fun `Permissions are read when the mode is picked, not when the launcher is built`() = scenario {
    m Given "permissions that get granted after the launcher was built"
    var granted = false
    val launcher = launcher(PaECampaignType.PLAY_TIME, hasUsagePermissions = { granted })
    granted = true

    m When "the mode is picked"
    val mode = launcher.mode()

    m Then "the current permission state counts"
    assertEquals(PaEPlayMode.TRACKING, mode)
  }

  @Test
  fun `Tracking mode starts the tracking service and opens the game`() = scenario {
    m Given "a play-time campaign with the permissions"
    val calls = Calls()

    m When "Play is launched"
    launcher(PaECampaignType.PLAY_TIME).launch(calls)

    m Then "the tracking service starts, the game opens, nothing else"
    assertEquals(
      listOf(1, 1, null, 0),
      listOf(calls.tracking, calls.open, calls.status, calls.permissions),
    )
  }

  @Test
  fun `Status mode starts the heartbeat for the game and opens it`() = scenario {
    m Given "an event campaign"
    val calls = Calls()

    m When "Play is launched"
    launcher(PaECampaignType.EVENT).launch(calls)

    m Then "the status heartbeat starts for that game, the game opens, nothing else"
    assertEquals("com.game", calls.status)
    assertEquals(listOf(0, 1, 0), listOf(calls.tracking, calls.open, calls.permissions))
  }

  @Test
  fun `Requesting the permissions does not open the game`() = scenario {
    m Given "a play-time campaign without the permissions"
    val calls = Calls()

    m When "Play is launched"
    launcher(PaECampaignType.PLAY_TIME, hasUsagePermissions = { false }).launch(calls)

    m Then "only the permission screens are requested"
    assertEquals(
      listOf(1, 0, 0, null),
      listOf(calls.permissions, calls.open, calls.tracking, calls.status),
    )
  }

  @Test
  fun `Only one combination tracks and only one asks for the permissions`() = scenario {
    m Given "every combination of type, kill switch, opt-in, permissions and navigation"
    val cases = buildList {
      for (type in listOf(PaECampaignType.PLAY_TIME, PaECampaignType.EVENT, null)) {
        for (flag in listOf(true, false)) for (optedIn in listOf(true, false)) {
          for (perms in listOf(true, false)) for (nav in listOf(true, false)) {
            add(listOf(type, flag, optedIn, perms, nav))
          }
        }
      }
    }

    m When "the mode is picked for each one"
    val modes = cases.associateWith { (type, flag, optedIn, perms, nav) ->
      launcher(
        type as PaECampaignType?,
        usageTrackingEnabled = flag as Boolean,
        optedIn = optedIn as Boolean,
        hasUsagePermissions = { perms as Boolean },
        canRequestPermissions = nav as Boolean,
      ).mode()
    }

    m Then "only play-time+flag+opted in tracks (with permissions) or asks (without); else status"
    assertEquals(48, cases.size)
    assertEquals(
      setOf(
        listOf(PaECampaignType.PLAY_TIME, true, true, true, true),
        listOf(PaECampaignType.PLAY_TIME, true, true, true, false),
      ),
      modes.filterValues { it == PaEPlayMode.TRACKING }.keys,
    )
    assertEquals(
      setOf(listOf(PaECampaignType.PLAY_TIME, true, true, false, true)),
      modes.filterValues { it == PaEPlayMode.REQUEST_PERMISSIONS }.keys,
    )
    assertEquals(45, modes.values.count { it == PaEPlayMode.STATUS })
  }
}
