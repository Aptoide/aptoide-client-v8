package com.aptoide.android.aptoidegames.play_and_earn.presentation.app_view

import cm.aptoide.pt.campaigns.domain.PaEAttribution
import cm.aptoide.pt.campaigns.domain.PaEAttributionStatus
import cm.aptoide.pt.campaigns.domain.PaERewardsState
import cm.aptoide.pt.campaigns.domain.paeRewardsState
import cm.aptoide.pt.test.gherkin.scenario
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

internal class PaERewardsStateTest {

  companion object {
    private fun attribution(status: PaEAttributionStatus) = PaEAttribution(status = status)

    @JvmStatic
    fun apiValues() = listOf(
      Arguments.of("not_started", PaEAttributionStatus.NOT_STARTED),
      Arguments.of("active", PaEAttributionStatus.ACTIVE),
      Arguments.of("not_eligible", PaEAttributionStatus.NOT_ELIGIBLE),
      Arguments.of("completed", PaEAttributionStatus.COMPLETED),
      Arguments.of("expired", PaEAttributionStatus.EXPIRED),
      Arguments.of("NOT_ELIGIBLE", PaEAttributionStatus.NOT_ELIGIBLE),
      Arguments.of(" active ", PaEAttributionStatus.ACTIVE),
      Arguments.of("pending", null),
      Arguments.of("", null),
      Arguments.of(null, null),
    )

    @JvmStatic
    fun cases() = listOf(
      // No status: a guest, an older backend, or no campaign with a click link.
      Arguments.of(null, false, PaERewardsState.MISSIONS),
      Arguments.of(null, true, PaERewardsState.MISSIONS),
      // Not installed yet: the missions to go for.
      Arguments.of(PaEAttributionStatus.NOT_STARTED, false, PaERewardsState.MISSIONS),
      // Installed, but the game's tracking tool hasn't confirmed it yet.
      Arguments.of(PaEAttributionStatus.NOT_STARTED, true, PaERewardsState.PAUSED),
      Arguments.of(PaEAttributionStatus.ACTIVE, true, PaERewardsState.MISSIONS),
      Arguments.of(PaEAttributionStatus.COMPLETED, true, PaERewardsState.MISSIONS),
      Arguments.of(PaEAttributionStatus.EXPIRED, true, PaERewardsState.MISSIONS),
      Arguments.of(PaEAttributionStatus.NOT_ELIGIBLE, true, PaERewardsState.NOT_ELIGIBLE),
      Arguments.of(PaEAttributionStatus.NOT_ELIGIBLE, false, PaERewardsState.NOT_ELIGIBLE),
    )
  }

  @ParameterizedTest(name = "{0}, installed={1} -> {2}")
  @MethodSource("cases")
  fun `The rewards tab follows the attribution status`(
    status: PaEAttributionStatus?,
    installed: Boolean,
    expected: PaERewardsState,
  ) = scenario {
    m Given "the campaign status $status and the game installed=$installed"
    val attribution = status?.let(::attribution)

    m When "the rewards tab decides what to show"
    val state = paeRewardsState(attribution, isInstalled = installed)

    m Then "it shows $expected"
    assertEquals(expected, state)
  }

  @ParameterizedTest(name = "\"{0}\" -> {1}")
  @MethodSource("apiValues")
  fun `The backend status is read case-insensitively, unknown ones are dropped`(
    value: String?,
    expected: PaEAttributionStatus?,
  ) = scenario {
    m Given "the backend sends the status \"$value\""
    m When "it is parsed"
    val status = PaEAttributionStatus.fromApi(value)

    m Then "it is $expected"
    assertEquals(expected, status)
  }
}
