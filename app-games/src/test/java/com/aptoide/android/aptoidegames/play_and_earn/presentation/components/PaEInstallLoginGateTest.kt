package com.aptoide.android.aptoidegames.play_and_earn.presentation.components

import cm.aptoide.pt.test.gherkin.scenario
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

// AND-876: a Play & Earn install is only started by a signed-in user. The gate wraps the install
// action and either runs it or sends the user to sign in.
internal class PaEInstallLoginGateTest {

  private class Calls {
    var action = 0
    var login = 0
  }

  @Test
  fun `Signed-in user runs the action directly`() = scenario {
    m Given "a signed-in user with navigation available"
    val calls = Calls()
    val gate = PaEInstallLoginGate(isSignedIn = true, requestLogin = { calls.login++ })

    m When "the guarded action is tapped"
    gate.guard { calls.action++ }()

    m Then "the action runs and no login is requested"
    assertEquals(1, calls.action)
    assertEquals(0, calls.login)
  }

  @Test
  fun `Logged-out user is sent to sign in instead of running the action`() = scenario {
    m Given "a logged-out user with navigation available"
    val calls = Calls()
    val gate = PaEInstallLoginGate(isSignedIn = false, requestLogin = { calls.login++ })

    m When "the guarded action is tapped"
    gate.guard { calls.action++ }()

    m Then "login is requested and the action does not run"
    assertEquals(0, calls.action)
    assertEquals(1, calls.login)
  }

  @Test
  fun `Unknown sign-in state runs the action as before the gate existed`() = scenario {
    m Given "the sign-in state has not been resolved yet"
    val calls = Calls()
    val gate = PaEInstallLoginGate(isSignedIn = null, requestLogin = { calls.login++ })

    m When "the guarded action is tapped"
    gate.guard { calls.action++ }()

    m Then "the action runs and no login is requested"
    assertEquals(1, calls.action)
    assertEquals(0, calls.login)
  }

  @Test
  fun `Logged-out user without navigation runs the action`() = scenario {
    m Given "a logged-out user in a surface that cannot navigate"
    val calls = Calls()
    val gate = PaEInstallLoginGate(isSignedIn = false, requestLogin = null)

    m When "the guarded action is tapped"
    gate.guard { calls.action++ }()

    m Then "the action runs"
    assertEquals(1, calls.action)
  }

  @Test
  fun `The None gate never asks for login`() = scenario {
    m Given "the gate that never asks for login"
    val calls = Calls()
    val gate = PaEInstallLoginGate.None

    m When "the guarded action is tapped"
    gate.guard { calls.action++ }()

    m Then "the action runs"
    assertEquals(1, calls.action)
  }

  @Test
  fun `Each tap is evaluated again`() = scenario {
    m Given "a logged-out user with navigation available"
    val calls = Calls()
    val gate = PaEInstallLoginGate(isSignedIn = false, requestLogin = { calls.login++ })
    val guarded = gate.guard { calls.action++ }

    m When "the guarded action is tapped twice"
    guarded()
    guarded()

    m Then "login is requested both times and the action never runs"
    assertEquals(0, calls.action)
    assertEquals(2, calls.login)
  }
}
