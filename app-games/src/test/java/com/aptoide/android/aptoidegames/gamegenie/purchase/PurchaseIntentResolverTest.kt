package com.aptoide.android.aptoidegames.gamegenie.purchase

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

internal class PurchaseIntentResolverTest {

  @ParameterizedTest
  @ValueSource(
    strings = [
      "buy extra rolls in Diceroll",
      "BUY EXTRA ROLLS IN DICEROLL!",
      "Can you purchase 3 rolls for dice roll?",
      "get me more attempts in diceroll",
      "Quero comprar jogadas no Diceroll",
      "compra rolls no Dice Roll",
      "Quéro mais jogadas no Dícéroll",
    ]
  )
  fun `a buy verb and a game alias resolve to the catalogue item`(text: String) {
    assertEquals(GeniePurchaseCatalog.diceroll, PurchaseIntentResolver.resolve(text))
  }

  @ParameterizedTest
  @ValueSource(
    strings = [
      "",
      "what is diceroll about?",
      "buy extra rolls",
      "buy rolls in Royal Match",
      "buyer guide for dicerolling",
      "I forgot my diceroll password",
    ]
  )
  fun `no buy verb or no catalogue game resolves to nothing`(text: String) {
    assertNull(PurchaseIntentResolver.resolve(text))
  }
}
