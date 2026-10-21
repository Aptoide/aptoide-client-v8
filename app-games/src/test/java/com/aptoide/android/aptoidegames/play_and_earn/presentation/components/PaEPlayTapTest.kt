package com.aptoide.android.aptoidegames.play_and_earn.presentation.components

import cm.aptoide.pt.test.gherkin.scenario
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

// AND-901: the sign-in gate of Play. Whether the game opens or the permission screens do is the
// launcher's call, so a signed-in tap hands `open` over instead of running it.
internal class PaEPlayTapTest {

  private class Calls {
    var open = 0
    var signIn = 0
    /** What the launcher was handed; it decides when (and whether) to run it. */
    var handedOver: (() -> Unit)? = null
  }

  private fun tap(isSignedIn: Boolean?, canNavigate: Boolean = true, calls: Calls) = PaEPlayTap(
    isSignedIn = isSignedIn,
    requestSignIn = if (canNavigate) ({ calls.signIn++ }) else null,
    play = { open -> calls.handedOver = open },
  )

  @Test
  fun `Signed-in user hands the game's open action to the launcher`() = scenario {
    m Given "a signed-in user"
    val calls = Calls()
    val tap = tap(isSignedIn = true, calls = calls)

    m When "Play is tapped"
    tap.guard { calls.open++ }()

    m Then "the game has not opened by itself: only the launcher can open it, and no sign-in"
    assertEquals(0, calls.open)
    assertEquals(0, calls.signIn)
    calls.handedOver!!.invoke()
    assertEquals(1, calls.open)
  }

  @Test
  fun `Signed-in user on a surface that cannot navigate still goes through the launcher`() =
    scenario {
      m Given "a signed-in user on a surface that cannot navigate"
      val calls = Calls()
      val tap = tap(isSignedIn = true, canNavigate = false, calls = calls)

      m When "Play is tapped"
      tap.guard { calls.open++ }()

      m Then "the launcher got the tap and the game did not open by itself"
      assertEquals(0, calls.open)
      assertEquals(true, calls.handedOver != null)
    }

  @Test
  fun `Logged-out user is sent to sign in`() = scenario {
    m Given "a logged-out user on a surface that can navigate"
    val calls = Calls()
    val tap = tap(isSignedIn = false, calls = calls)

    m When "Play is tapped"
    tap.guard { calls.open++ }()

    m Then "sign-in is requested, nothing plays, the game does not open"
    assertEquals(1, calls.signIn)
    assertEquals(null, calls.handedOver)
    assertEquals(0, calls.open)
  }

  @Test
  fun `Logged-out user without navigation just opens the game`() = scenario {
    m Given "a logged-out user on a surface that cannot navigate"
    val calls = Calls()
    val tap = tap(isSignedIn = false, canNavigate = false, calls = calls)

    m When "Play is tapped"
    tap.guard { calls.open++ }()

    m Then "the game opens directly, nothing else happens"
    assertEquals(1, calls.open)
    assertEquals(null, calls.handedOver)
    assertEquals(0, calls.signIn)
  }

  @Test
  fun `Unknown sign-in state opens the game directly`() = scenario {
    m Given "the sign-in state has not been resolved yet"
    val calls = Calls()
    val tap = tap(isSignedIn = null, calls = calls)

    m When "Play is tapped"
    tap.guard { calls.open++ }()

    m Then "the game opens, no sign-in is requested, the launcher is not involved"
    assertEquals(1, calls.open)
    assertEquals(0, calls.signIn)
    assertEquals(null, calls.handedOver)
  }
}
