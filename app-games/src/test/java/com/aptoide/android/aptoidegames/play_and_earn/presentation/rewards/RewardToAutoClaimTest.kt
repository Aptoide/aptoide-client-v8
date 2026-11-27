package com.aptoide.android.aptoidegames.play_and_earn.presentation.rewards

import cm.aptoide.pt.test.gherkin.scenario
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

// AND-953: back from the reward sign-in, the bonus is claimed once it is offered, even when the
// sign-in made it load again.
internal class RewardToAutoClaimTest {

  private val reward = PendingPaEReward(PaERewardType.ROBUX, "$1.00", 100)

  @Test
  fun `Nothing is claimed while the bonus is still loading after the sign-in`() = scenario {
    m Given "a user back from the reward sign-in, signed in, the bonus loading again"
    m When "the reward to claim is chosen"
    val toClaim = rewardToAutoClaim(
      awaitingSignIn = true,
      signedIn = true,
      rewardState = RewardState.Loading,
    )

    m Then "there is none yet"
    assertNull(toClaim)
  }

  @Test
  fun `The bonus is claimed once it is offered`() = scenario {
    m Given "a user back from the reward sign-in, signed in, the bonus offered"
    m When "the reward to claim is chosen"
    val toClaim = rewardToAutoClaim(
      awaitingSignIn = true,
      signedIn = true,
      rewardState = RewardState.Unclaimed(reward),
    )

    m Then "it is the offered reward"
    assertEquals(reward, toClaim)
  }

  @Test
  fun `Nothing is claimed once the claim was made`() = scenario {
    m Given "a claim already sent, so the surface no longer awaits the sign-in"
    m When "the reward state changes again and the reward to claim is chosen"
    val toClaim = rewardToAutoClaim(
      awaitingSignIn = false,
      signedIn = true,
      rewardState = RewardState.Unclaimed(reward),
    )

    m Then "there is none: one claim only"
    assertNull(toClaim)
  }

  @Test
  fun `Nothing is claimed for a user who came back signed out`() = scenario {
    m Given "a user back from the reward sign-in without signing in"
    m When "the reward to claim is chosen"
    val toClaim = rewardToAutoClaim(
      awaitingSignIn = true,
      signedIn = false,
      rewardState = RewardState.Unclaimed(reward),
    )

    m Then "there is none"
    assertNull(toClaim)
  }

  @Test
  fun `Nothing is claimed for a refused or claimed bonus`() = scenario {
    m Given "a user back from the reward sign-in, signed in"
    m When "the bonus is refused, or already claimed"
    val refused = rewardToAutoClaim(true, signedIn = true, rewardState = RewardState.Refused)
    val claimed = rewardToAutoClaim(true, signedIn = true, rewardState = RewardState.Claimed)

    m Then "there is nothing to claim"
    assertNull(refused)
    assertNull(claimed)
  }
}
