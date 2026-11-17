package com.aptoide.android.aptoidegames.gamegenie.purchase

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

internal class GeniePerkLinkTest {

  private val diceroll = GeniePurchaseCatalog.diceroll

  @Test
  fun `a perk link for a catalogue item resolves to that item and its perk`() {
    val link = "ag://genie/buy?package=com.aptoide.diceroll.sdk.dev&sku=attempts" +
      "&perk=20%25%20bonus%20on%20Extra%20attempts"

    val request = GeniePerkLink.parse(link, deepLinkScheme = "ag")

    assertEquals(GeniePerkRequest(diceroll, "20% bonus on Extra attempts"), request)
  }

  @Test
  fun `build and parse round-trip, including the prev-screen param the app appends`() {
    val link = GeniePerkLink.build("ag://", "com.aptoide.diceroll.sdk", "attempts", "Bonus & more")

    val request = GeniePerkLink.parse("$link&prevScreen=notification", deepLinkScheme = "ag")

    assertEquals(GeniePerkRequest(diceroll, "Bonus & more"), request)
  }

  @Test
  fun `the perk is optional and sanitized`() {
    val base = "ag://genie/buy?package=com.aptoide.diceroll.sdk.dev&sku=attempts"

    assertEquals(GeniePerkRequest(diceroll, null), GeniePerkLink.parse(base, "ag"))
    assertEquals(
      GeniePerkRequest(diceroll, null),
      GeniePerkLink.parse("$base&perk=%20%20", "ag")
    )
    assertEquals(
      "a".repeat(120),
      GeniePerkLink.parse("$base&perk=${"a".repeat(500)}", "ag")?.perk
    )
    assertEquals(
      "line one  line two",
      GeniePerkLink.parse("$base&perk=line%20one%0A%0Dline%20two", "ag")?.perk
    )
  }

  @Test
  fun `the first parameter wins over a duplicate`() {
    val link = "ag://genie/buy?package=com.aptoide.diceroll.sdk.dev&sku=attempts" +
      "&package=com.evil.app"

    assertEquals(diceroll, GeniePerkLink.parse(link, "ag")?.item)
  }

  @ParameterizedTest
  @ValueSource(
    strings = [
      "",
      "not a uri at all %%%",
      // wrong scheme, host or path
      "aptoide://genie/buy?package=com.aptoide.diceroll.sdk.dev&sku=attempts",
      "ag://shop/buy?package=com.aptoide.diceroll.sdk.dev&sku=attempts",
      "ag://genie/purchase-result?package=com.aptoide.diceroll.sdk.dev&sku=attempts",
      // package or sku missing, or not in the catalogue
      "ag://genie/buy?sku=attempts",
      "ag://genie/buy?package=com.aptoide.diceroll.sdk.dev",
      "ag://genie/buy?package=com.evil.app&sku=attempts",
      "ag://genie/buy?package=com.aptoide.diceroll.sdk.dev&sku=not_in_catalogue",
    ]
  )
  fun `links that are not a perk link for a catalogue item are rejected`(link: String) {
    assertNull(GeniePerkLink.parse(link, deepLinkScheme = "ag"))
  }

  @Test
  fun `the intro mentions the perk when there is one`() {
    assertEquals(
      "Diceroll has a perk for you today: 20% bonus. Let's get it for you.",
      GeniePerkLink.introMessage(GeniePerkRequest(diceroll, "20% bonus"))
    )
    assertEquals(
      "Let's get Extra attempts in Diceroll for you.",
      GeniePerkLink.introMessage(GeniePerkRequest(diceroll, null))
    )
  }
}
