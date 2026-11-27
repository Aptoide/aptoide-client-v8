package com.aptoide.android.aptoidegames.play_and_earn.presentation.rewards

import cm.aptoide.pt.test.gherkin.scenario
import com.aptoide.android.aptoidegames.play_and_earn.domain.PaEAccountRestrictionState
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

// AND-951: the home sign-in bonus card is not shown to a restricted account.
internal class SignInBonusOfferTest {

  private val offered = RewardState.Unclaimed(PendingPaEReward(PaERewardType.ROBUX, "$1.00", 100))

  @Test
  fun `A restricted account is not offered the bonus`() = scenario {
    m Given "a bonus offered to an account the backend has restricted"
    m When "the home feed decides whether to show the card"
    val offer = signInBonusOffer(offered, PaEAccountRestrictionState.RESTRICTED)

    m Then "there is no card"
    assertNull(offer)
  }

  @Test
  fun `The bonus is offered while the account's answer is on its way`() = scenario {
    m Given "a bonus offered, the account not answered about yet"
    m When "the home feed decides whether to show the card"
    val offer = signInBonusOffer(offered, PaEAccountRestrictionState.UNKNOWN)

    m Then "the card shows, as before"
    assertEquals(offered, offer)
  }

  @Test
  fun `An account in good standing is offered the bonus`() = scenario {
    m Given "a bonus offered to an account the backend says is fine"
    m When "the home feed decides whether to show the card"
    val offer = signInBonusOffer(offered, PaEAccountRestrictionState.NOT_RESTRICTED)

    m Then "the card shows"
    assertEquals(offered, offer)
  }

  @Test
  fun `A bonus not offered stays hidden`() = scenario {
    m Given "a bonus already claimed by an account in good standing"
    m When "the home feed decides whether to show the card"
    val offer = signInBonusOffer(RewardState.Claimed, PaEAccountRestrictionState.NOT_RESTRICTED)

    m Then "there is no card"
    assertNull(offer)
  }
}
