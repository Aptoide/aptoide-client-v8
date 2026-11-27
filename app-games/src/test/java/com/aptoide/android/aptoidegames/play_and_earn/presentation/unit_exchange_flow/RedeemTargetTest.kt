package com.aptoide.android.aptoidegames.play_and_earn.presentation.unit_exchange_flow

import cm.aptoide.pt.test.gherkin.scenario
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigDecimal

// AND-963: Level Up's card and the redeem screen it opens count the missing units, and write
// the redeem's amount, with the same two functions, so they can never disagree.
internal class RedeemTargetTest {

  @Test
  fun `Units below the target are still missing`() = scenario {
    m Given "a redeem that needs 500 units"
    val target = 500L

    m When "the balance is below it"
    val missing = listOf(0L, 99L, 499L).map { unitsToRedeem(it, target) }

    m Then "the missing units are what is left to the target"
    assertEquals(listOf(500L, 401L, 1L), missing)
  }

  @Test
  fun `At the target and above nothing is missing`() = scenario {
    m Given "a redeem that needs 500 units"
    val target = 500L

    m When "the balance reaches or passes it"
    val missing = listOf(500L, 12_000L).map { unitsToRedeem(it, target) }

    m Then "the redeem is offered, never a negative number of units"
    assertEquals(listOf(0L, 0L), missing)
  }

  @Test
  fun `The redeem amount is written with two decimals`() = scenario {
    m Given "amounts with none, two and three decimals"
    val amounts = listOf(BigDecimal("5"), REDEEM_AMOUNT, BigDecimal("2.345"))

    m When "they are written for the card and the redeem screen"
    val written = amounts.map(::formattedRedeemAmount)

    m Then "each has a dollar sign and two decimals"
    assertEquals(listOf("$5.00", "$5.00", "$2.35"), written)
  }
}
