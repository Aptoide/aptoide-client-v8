package com.aptoide.android.aptoidegames.play_and_earn.presentation.overlays

import cm.aptoide.pt.test.gherkin.scenario
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.random.Random

// Hackathon 2026 (T5): the numbers behind the Claim animation of the mission-completed overlay,
// taken from the designer's prototype.
internal class MissionClaimAnimationTest {

  private val plan =
    CoinFlightPlan(spreadX = 30f, spreadY = -40f, durationMillis = 1000, delayMillis = 0)
  private val linear: (Float) -> Float = { it }

  private fun frame(progress: Float) =
    coinFrame(progress, plan, targetX = 200f, targetY = -50f, easing = linear)

  // The legs' boundaries are not exact in floating point.
  private fun assertClose(expected: CoinFrame, actual: CoinFrame) {
    assertEquals(expected.x, actual.x, 0.01f)
    assertEquals(expected.y, actual.y, 0.01f)
    assertEquals(expected.scale, actual.scale, 0.001f)
    assertEquals(expected.alpha, actual.alpha, 0.001f)
  }

  @Test
  fun `A coin starts small and invisible on the Claim button`() = scenario {
    m Given "a coin about to leave the button"

    m When "its flight starts"
    val frame = frame(0f)

    m Then "it sits on the button, scaled down and transparent"
    assertEquals(CoinFrame(x = 0f, y = 0f, scale = 0.4f, alpha = 0f), frame)
  }

  @Test
  fun `A coin first spreads away from the button at full size`() = scenario {
    m Given "a coin in flight"

    m When "the spread ends, 35 percent into the flight"
    val frame = frame(0.35f)

    m Then "it is at its spread point, full size and opaque"
    assertEquals(CoinFrame(x = 30f, y = -40f, scale = 1f, alpha = 1f), frame)
  }

  @Test
  fun `A coin then flies to the balance, shrinking on the way`() = scenario {
    m Given "a coin in flight"

    m When "it is halfway from the spread point and when it arrives"
    val halfway = frame(0.65f)
    val arrived = frame(0.95f)

    m Then "it moves in a straight line and lands on the balance still opaque"
    assertClose(CoinFrame(x = 115f, y = -45f, scale = 0.8f, alpha = 1f), halfway)
    assertClose(CoinFrame(x = 200f, y = -50f, scale = 0.6f, alpha = 1f), arrived)
  }

  @Test
  fun `A coin fades out on the balance, and stays gone`() = scenario {
    m Given "a coin that reached the balance"

    m When "its flight ends, and a frame arrives past the end"
    val end = frame(1f)
    val past = frame(1.4f)

    m Then "it is on the balance, tiny and transparent"
    assertEquals(CoinFrame(x = 200f, y = -50f, scale = 0.3f, alpha = 0f), end)
    assertEquals(end, past)
  }

  @Test
  fun `Coins leave one after the other, spread around the button`() = scenario {
    m Given "the twelve coins of a claim"
    val random = Random(7)

    m When "their flights are planned"
    val plans = (0 until 12).map { coinFlightPlan(index = it, count = 12, random = random) }

    m Then "each leaves 45 ms after the previous, lasts 900 to 1150 ms, within the window"
    assertEquals((0 until 12).map { it * 45 }, plans.map { it.delayMillis })
    assertTrue(plans.all { it.durationMillis in 900..1150 })
    assertTrue(plans.all { it.spreadX in -80f..80f && it.spreadY in -60f..100f })
    assertTrue(plans.all { it.delayMillis + it.durationMillis <= COINS_FLIGHT_MILLIS })
    assertTrue(plans.any { it.spreadX < 0 } && plans.any { it.spreadX > 0 })
  }

  @Test
  fun `The balance counts from before the reward up to the wallet's balance`() = scenario {
    m Given "a wallet that holds 1490 units once a 150 unit mission was rewarded"

    m When "the count is planned"
    val count = claimBalanceCount(balanceAfter = 1490, units = 150)

    m Then "it runs from 1340 to 1490"
    assertEquals(BalanceCount(from = 1340, to = 1490, isWalletBalance = true), count)
    assertEquals(1340, count.at(0f))
    assertEquals(1415, count.at(0.5f))
    assertEquals(1490, count.at(1f))
  }

  @Test
  fun `A balance smaller than the reward never counts from below zero`() = scenario {
    m Given "a wallet read before the reward landed in it"

    m When "the count is planned"
    val count = claimBalanceCount(balanceAfter = 100, units = 150)

    m Then "it starts at zero"
    assertEquals(BalanceCount(from = 0, to = 100, isWalletBalance = true), count)
  }

  @Test
  fun `Without the wallet's balance the reward itself is counted`() = scenario {
    m Given "a wallet that could not be read in time"

    m When "the count is planned"
    val count = claimBalanceCount(balanceAfter = null, units = 150)

    m Then "the chip counts the reward from zero, marked as not being the balance"
    assertEquals(BalanceCount(from = 0, to = 150, isWalletBalance = false), count)
  }
}
