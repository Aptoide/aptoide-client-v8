package com.aptoide.android.aptoidegames.play_and_earn.domain.sessions

import cm.aptoide.pt.campaigns.domain.PaEAttribution
import cm.aptoide.pt.campaigns.domain.PaEAttributionStatus
import cm.aptoide.pt.campaigns.domain.PaEMission
import cm.aptoide.pt.campaigns.domain.PaEMissionProgress
import cm.aptoide.pt.campaigns.domain.PaEMissionProgressType
import cm.aptoide.pt.campaigns.domain.PaEMissionStatus
import cm.aptoide.pt.campaigns.domain.PaEMissionType
import cm.aptoide.pt.campaigns.domain.PaEMissions
import cm.aptoide.pt.test.gherkin.scenario
import com.google.gson.JsonObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit

internal class PaEStatusHeartbeatPolicyTest {

  private val startedAt = 1_000_000L
  private val policy = PaEStatusHeartbeatPolicy(
    startedAtMillis = startedAt,
    screenOffLimitMillis = TimeUnit.MINUTES.toMillis(10),
    maxDurationMillis = TimeUnit.HOURS.toMillis(10),
  )

  private fun mission(title: String, status: PaEMissionStatus?) = PaEMission(
    title = title,
    description = null,
    icon = null,
    type = PaEMissionType.EVENT,
    arguments = JsonObject(),
    units = 10,
    progress = PaEMissionProgress(
      current = if (status == PaEMissionStatus.COMPLETED) 1 else 0,
      target = 1,
      type = PaEMissionProgressType.COUNT,
      status = status,
    ),
  )

  private fun missions(vararg items: PaEMission, attribution: PaEAttribution? = null) =
    PaEMissions(checkpoints = emptyList(), missions = items.toList(), attribution = attribution)

  @Test
  fun `Keeps going while a mission is still open and the screen is on`() = scenario {
    m Given "an open mission, the screen on, a few minutes in"
    val state = missions(mission("Buy", PaEMissionStatus.PENDING))

    m When "the policy is asked"
    val reason = policy.stopReason(state, confirmed = emptySet(), nowMillis = startedAt + 60_000)

    m Then "there is no reason to stop"
    assertNull(reason)
  }

  @Test
  fun `Stops once every mission is done`() = scenario {
    m Given "the only mission is confirmed by the server in this run"
    val state = missions(mission("Buy", PaEMissionStatus.PENDING))

    m When "the policy is asked"
    val reason = policy.stopReason(state, confirmed = setOf("Buy"), nowMillis = startedAt + 60_000)

    m Then "nothing is left to wait for"
    assertEquals(PaEStatusHeartbeatPolicy.StopReason.ALL_DONE, reason)
  }

  @Test
  fun `Missions already completed before this run count as done`() = scenario {
    m Given "every mission already completed on the server"
    val state = missions(mission("Buy", PaEMissionStatus.COMPLETED))

    m When "the policy is asked"
    val reason = policy.stopReason(state, confirmed = emptySet(), nowMillis = startedAt + 60_000)

    m Then "nothing is left to wait for"
    assertEquals(PaEStatusHeartbeatPolicy.StopReason.ALL_DONE, reason)
  }

  @Test
  fun `A game with no missions is nothing to wait for`() = scenario {
    m Given "no missions at all"
    val state = missions()

    m When "the policy is asked"
    val reason = policy.stopReason(state, confirmed = emptySet(), nowMillis = startedAt + 60_000)

    m Then "it stops"
    assertEquals(PaEStatusHeartbeatPolicy.StopReason.ALL_DONE, reason)
  }

  @Test
  fun `Stops when the user is not eligible`() = scenario {
    m Given "an open mission but a rejected install"
    val state = missions(
      mission("Buy", PaEMissionStatus.PENDING),
      attribution = PaEAttribution(PaEAttributionStatus.NOT_ELIGIBLE, "rejected_fraud"),
    )

    m When "the policy is asked"
    val reason = policy.stopReason(state, confirmed = emptySet(), nowMillis = startedAt + 60_000)

    m Then "it stops: nothing will be paid"
    assertEquals(PaEStatusHeartbeatPolicy.StopReason.NOT_ELIGIBLE, reason)
  }

  @Test
  fun `Stops after ten minutes of screen off, not before`() = scenario {
    m Given "an open mission and the screen switched off"
    val state = missions(mission("Buy", PaEMissionStatus.PENDING))
    policy.onScreenOff(nowMillis = startedAt + 60_000)

    m When "nine minutes pass, then eleven"
    val early = policy.stopReason(state, emptySet(), nowMillis = startedAt + 60_000 + 9 * 60_000)
    val late = policy.stopReason(state, emptySet(), nowMillis = startedAt + 60_000 + 11 * 60_000)

    m Then "only the later check stops"
    assertNull(early)
    assertEquals(PaEStatusHeartbeatPolicy.StopReason.SCREEN_OFF, late)
  }

  @Test
  fun `Screen back on resets the screen-off timer`() = scenario {
    m Given "the screen went off, then on again"
    val state = missions(mission("Buy", PaEMissionStatus.PENDING))
    policy.onScreenOff(nowMillis = startedAt + 60_000)
    policy.onScreenOn()

    m When "much later the policy is asked"
    val reason = policy.stopReason(state, emptySet(), nowMillis = startedAt + 60 * 60_000)

    m Then "the screen-off limit doesn't apply"
    assertNull(reason)
  }

  @Test
  fun `Heartbeats pause while the screen is off`() = scenario {
    m Given "the screen switched off"
    policy.onScreenOff(nowMillis = startedAt + 60_000)

    m When "asked whether to send a heartbeat"
    val paused = policy.isPaused

    m Then "it is paused"
    assertTrue(paused)
  }

  @Test
  fun `Stops at the backstop whatever else is going on`() = scenario {
    m Given "an open mission, the screen on, ten hours later"
    val state = missions(mission("Buy", PaEMissionStatus.PENDING))

    m When "the policy is asked"
    val reason =
      policy.stopReason(state, emptySet(), nowMillis = startedAt + TimeUnit.HOURS.toMillis(10))

    m Then "the backstop stops it"
    assertEquals(PaEStatusHeartbeatPolicy.StopReason.MAX_DURATION, reason)
  }

  @Test
  fun `Open checkpoints are not waited for`() = scenario {
    m Given "every mission done, a checkpoint still open"
    val state = PaEMissions(
      checkpoints = listOf(mission("Checkpoint", PaEMissionStatus.PENDING)),
      missions = listOf(mission("Buy", PaEMissionStatus.COMPLETED)),
    )

    m When "the policy is asked"
    val reason = policy.stopReason(state, emptySet(), nowMillis = startedAt + 60_000)

    m Then "nothing is left to wait for"
    assertEquals(PaEStatusHeartbeatPolicy.StopReason.ALL_DONE, reason)
  }

  @Test
  fun `The backstop can be raised after start`() = scenario {
    m Given "the default one-hour backstop raised to ten hours"
    policy.maxDurationMillis = TimeUnit.HOURS.toMillis(10)
    val state = missions(mission("Buy", PaEMissionStatus.PENDING))

    m When "two hours have passed"
    val reason =
      policy.stopReason(state, emptySet(), nowMillis = startedAt + TimeUnit.HOURS.toMillis(2))

    m Then "it keeps going"
    assertNull(reason)
  }

  @Test
  fun `Without missions data it keeps going until another limit`() = scenario {
    m Given "the missions couldn't be loaded"
    m When "the policy is asked"
    val reason = policy.stopReason(null, emptySet(), nowMillis = startedAt + 60_000)

    m Then "it doesn't stop on missing data"
    assertNull(reason)
  }
}
