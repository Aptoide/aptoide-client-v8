package com.aptoide.android.aptoidegames.ads

import cm.aptoide.pt.test.gherkin.scenario
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class AdRetryPolicyTest {

  @Test
  fun `Delays double from the base and are capped`() = scenario {
    m Given "a policy with a 2s base, 10s cap and 5 attempts"
    val policy = AdRetryPolicy(baseDelayMs = 2_000, maxDelayMs = 10_000, maxAttempts = 5)

    m When "the delay of each attempt is read"
    val delays = (0..6).map { policy.delayForAttempt(it) }

    m Then "attempt 0 and attempts past the limit are null, the rest double up to the cap"
    assertEquals(listOf(null, 2_000L, 4_000L, 8_000L, 10_000L, 10_000L, null), delays)
  }

  @Test
  fun `Zero attempts means no retry at all`() = scenario {
    m Given "a policy with no retries"
    val policy = AdRetryPolicy(maxAttempts = 0)

    m When "the first retry delay is read"
    val delay = policy.delayForAttempt(1)

    m Then "there is none"
    assertNull(delay)
  }

  @Test
  fun `Re-evaluation is allowed only after the interval`() = scenario {
    m Given "a 60s re-evaluation interval"
    val policy = AdRetryPolicy(reevaluateIntervalMs = 60_000)

    m When "checked before and after the interval"
    val early = policy.canReevaluate(lastEvaluationAtMs = 1_000, nowMs = 30_000)
    val late = policy.canReevaluate(lastEvaluationAtMs = 1_000, nowMs = 61_000)

    m Then "only the later check passes"
    assertFalse(early)
    assertTrue(late)
  }
}
