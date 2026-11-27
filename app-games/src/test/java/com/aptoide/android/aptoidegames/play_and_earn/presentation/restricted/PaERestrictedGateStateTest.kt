package com.aptoide.android.aptoidegames.play_and_earn.presentation.restricted

import cm.aptoide.pt.test.gherkin.scenario
import com.aptoide.android.aptoidegames.play_and_earn.domain.PaEAccountRestrictionState
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

// AND-951: what a Play & Earn screen shows for what is known about the account.
internal class PaERestrictedGateStateTest {

  @Test
  fun `A restricted account sees the restricted screen`() = scenario {
    m Given "a signed-in account the backend has restricted"
    val restriction = PaEAccountRestrictionState.RESTRICTED

    m When "a Play & Earn screen decides what to show"
    val gate = paERestrictedGateStateOf(restriction, settled = true, signedIn = true)

    m Then "it shows the restricted screen instead of its content, held or not"
    assertEquals(PaERestrictedGateState.RESTRICTED, gate)
    assertFalse(gate.showsContent(holdWhileLoading = false))
    assertFalse(gate.showsContent(holdWhileLoading = true))
  }

  @ParameterizedTest(name = "{0}")
  @EnumSource(PaEAccountRestrictionState::class)
  fun `A signed-out user always sees the content`(restriction: PaEAccountRestrictionState) =
    scenario {
      m Given "nobody signed in, the restriction known as $restriction"
      m When "a Play & Earn screen decides what to show"
      val gate = paERestrictedGateStateOf(restriction, settled = true, signedIn = false)

      m Then "it shows its usual content"
      assertEquals(PaERestrictedGateState.OPEN, gate)
    }

  @Test
  fun `A screen shows its content while the answer is on its way`() = scenario {
    m Given "a signed-in account not answered about yet"
    val restriction = PaEAccountRestrictionState.UNKNOWN

    m When "a Play & Earn screen decides what to show"
    val gate = paERestrictedGateStateOf(restriction, settled = false, signedIn = true)

    m Then "it is loading: shown as usual, except on a screen that holds its content"
    assertEquals(PaERestrictedGateState.LOADING, gate)
    assertTrue(gate.showsContent(holdWhileLoading = false))
    assertFalse(gate.showsContent(holdWhileLoading = true))
  }

  @Test
  fun `A screen is loading until it is known who is signed in`() = scenario {
    m Given "the stored sign-in not read yet"
    m When "a Play & Earn screen decides what to show"
    val gate = paERestrictedGateStateOf(
      PaEAccountRestrictionState.UNKNOWN,
      settled = false,
      signedIn = null,
    )

    m Then "it is loading"
    assertEquals(PaERestrictedGateState.LOADING, gate)
  }

  @Test
  fun `A status the backend could not tell opens the screen`() = scenario {
    m Given "a signed-in account whose status call could not tell"
    m When "a Play & Earn screen decides what to show"
    val gate = paERestrictedGateStateOf(
      PaEAccountRestrictionState.UNKNOWN,
      settled = true,
      signedIn = true,
    )

    m Then "it shows its content, even on a screen that holds it: no waiting for ever"
    assertEquals(PaERestrictedGateState.OPEN, gate)
    assertTrue(gate.showsContent(holdWhileLoading = true))
  }

  @Test
  fun `An account in good standing sees the content`() = scenario {
    m Given "a signed-in account the backend says is fine"
    m When "a Play & Earn screen decides what to show"
    val gate = paERestrictedGateStateOf(
      PaEAccountRestrictionState.NOT_RESTRICTED,
      settled = true,
      signedIn = true,
    )

    m Then "it shows its usual content"
    assertEquals(PaERestrictedGateState.OPEN, gate)
    assertTrue(gate.showsContent(holdWhileLoading = true))
  }
}
