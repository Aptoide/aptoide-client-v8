package com.aptoide.android.aptoidegames.play_and_earn.presentation.restricted

import cm.aptoide.pt.test.gherkin.scenario
import com.aptoide.android.aptoidegames.play_and_earn.domain.PaEAccountRestrictionState
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

// AND-951: what a Play & Earn screen shows for what is known about the account.
internal class PaERestrictedGateStateTest {

  @Test
  fun `A restricted account sees the restricted screen`() = scenario {
    m Given "an account the backend has restricted"
    val restriction = PaEAccountRestrictionState.RESTRICTED

    m When "a Play & Earn screen decides what to show"
    val gate = restriction.toGateState()

    m Then "it shows the restricted screen instead of its content"
    assertEquals(PaERestrictedGateState.RESTRICTED, gate)
    assertFalse(gate.showsContent)
  }

  @Test
  fun `A screen shows its content while the answer is on its way`() = scenario {
    m Given "an account not answered about yet"
    val restriction = PaEAccountRestrictionState.UNKNOWN

    m When "a Play & Earn screen decides what to show"
    val gate = restriction.toGateState()

    m Then "it shows its usual content: no blank, no restricted screen flashing"
    assertEquals(PaERestrictedGateState.LOADING, gate)
    assertTrue(gate.showsContent)
  }

  @Test
  fun `An account in good standing sees the content`() = scenario {
    m Given "an account the backend says is fine"
    val restriction = PaEAccountRestrictionState.NOT_RESTRICTED

    m When "a Play & Earn screen decides what to show"
    val gate = restriction.toGateState()

    m Then "it shows its usual content"
    assertEquals(PaERestrictedGateState.OPEN, gate)
    assertTrue(gate.showsContent)
  }
}
