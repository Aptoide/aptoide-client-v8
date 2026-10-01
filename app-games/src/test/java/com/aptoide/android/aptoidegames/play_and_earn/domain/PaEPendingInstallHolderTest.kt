package com.aptoide.android.aptoidegames.play_and_earn.domain

import cm.aptoide.pt.test.gherkin.scenario
import com.aptoide.android.aptoidegames.play_and_earn.domain.PaEPendingInstallHolder.PendingInstall
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class PaEPendingInstallHolderTest {

  @Test
  fun `Starts with no pending install`() = scenario {
    m Given "a fresh holder"
    val holder = PaEPendingInstallHolder()

    m When "the pending install is read"
    val pending = holder.pending.value

    m Then "nothing is pending"
    assertNull(pending)
  }

  @Test
  fun `Request stores the install the user asked for`() = scenario {
    m Given "a fresh holder"
    val holder = PaEPendingInstallHolder()

    m When "an install is requested"
    holder.request(packageName = "com.game.one", appName = "Game One")

    m Then "it is the pending install"
    assertEquals(PendingInstall("com.game.one", "Game One"), holder.pending.value)
  }

  @Test
  fun `A new request replaces the previous pending install`() = scenario {
    m Given "a holder with a pending install"
    val holder = PaEPendingInstallHolder()
    holder.request(packageName = "com.game.one", appName = "Game One")

    m When "another install is requested"
    holder.request(packageName = "com.game.two", appName = "Game Two")

    m Then "only the latest one is pending"
    assertEquals(PendingInstall("com.game.two", "Game Two"), holder.pending.value)
  }

  @Test
  fun `Consume clears and returns true for the pending package`() = scenario {
    m Given "a holder with a pending install"
    val holder = PaEPendingInstallHolder()
    holder.request(packageName = "com.game.one", appName = "Game One")

    m When "the pending package is consumed"
    val consumed = holder.consume("com.game.one")

    m Then "it reports success and nothing is pending anymore"
    assertTrue(consumed)
    assertNull(holder.pending.value)
  }

  @Test
  fun `Consume keeps the pending install and returns false for another package`() = scenario {
    m Given "a holder with a pending install"
    val holder = PaEPendingInstallHolder()
    holder.request(packageName = "com.game.one", appName = "Game One")

    m When "a different package is consumed"
    val consumed = holder.consume("com.game.other")

    m Then "it reports failure and the original install stays pending"
    assertFalse(consumed)
    assertEquals(PendingInstall("com.game.one", "Game One"), holder.pending.value)
  }

  @Test
  fun `Consume returns false when nothing is pending`() = scenario {
    m Given "a fresh holder"
    val holder = PaEPendingInstallHolder()

    m When "a package is consumed"
    val consumed = holder.consume("com.game.one")

    m Then "it reports failure"
    assertFalse(consumed)
    assertNull(holder.pending.value)
  }

  @Test
  fun `Clear drops the pending install`() = scenario {
    m Given "a holder with a pending install"
    val holder = PaEPendingInstallHolder()
    holder.request(packageName = "com.game.one", appName = "Game One")

    m When "the holder is cleared"
    holder.clear()

    m Then "nothing is pending"
    assertNull(holder.pending.value)
  }
}
