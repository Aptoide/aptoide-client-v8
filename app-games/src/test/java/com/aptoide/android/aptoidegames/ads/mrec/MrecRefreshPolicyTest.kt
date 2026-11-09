package com.aptoide.android.aptoidegames.ads.mrec

import cm.aptoide.pt.test.gherkin.scenario
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class MrecRefreshPolicyTest {

  @Test
  fun `With auto-refresh off the timer is stopped after every load`() = scenario {
    m Given "the default policy with auto-refresh off"
    val policy = MrecRefreshPolicy(autoRefresh = false)

    m When "an ad loads while active and while inactive"
    val active = policy.onLoaded(active = true)
    val inactive = policy.onLoaded(active = false)

    m Then "the SDK's auto-started timer is stopped both times"
    assertEquals(MrecRefreshAction.STOP, active)
    assertEquals(MrecRefreshAction.STOP, inactive)
  }

  @Test
  fun `With auto-refresh off activity never starts the timer`() = scenario {
    m Given "auto-refresh off"
    val policy = MrecRefreshPolicy(autoRefresh = false)

    m When "the slot becomes active with a loaded ad"
    val resumed = policy.onActiveChanged(active = true, loaded = true)

    m Then "nothing is started"
    assertEquals(MrecRefreshAction.NONE, resumed)
  }

  @Test
  fun `With auto-refresh on the timer follows the host and stops when paused`() = scenario {
    m Given "auto-refresh switched on in Remote Config"
    val policy = MrecRefreshPolicy(autoRefresh = true)

    m When "the slot resumes with and without a loaded ad, and pauses"
    val resumedLoaded = policy.onActiveChanged(active = true, loaded = true)
    val resumedEmpty = policy.onActiveChanged(active = true, loaded = false)
    val paused = policy.onActiveChanged(active = false, loaded = true)
    val loadedWhilePaused = policy.onLoaded(active = false)
    val loadedWhileActive = policy.onLoaded(active = true)

    m Then "it starts for a loaded active ad (the SDK does not restart a stopped timer itself) and is stopped whenever the slot is inactive"
    assertEquals(MrecRefreshAction.START, resumedLoaded)
    assertEquals(MrecRefreshAction.NONE, resumedEmpty)
    assertEquals(MrecRefreshAction.STOP, paused)
    assertEquals(MrecRefreshAction.STOP, loadedWhilePaused)
    assertEquals(MrecRefreshAction.START, loadedWhileActive)
  }

  @Test
  fun `The documented SDK parameter name is used`() = scenario {
    m Given "the MAX banner and MREC integration guide"

    m When "the workaround parameter is read"
    val name = MrecRefreshPolicy.ALLOW_PAUSE_PARAMETER

    m Then "it matches the documented key"
    assertEquals("allow_pause_auto_refresh_immediately", name)
  }
}
