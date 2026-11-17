package com.aptoide.android.aptoidegames.appview.shop

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class AppShopLogicTest {

  @Test
  fun `the shop tab needs the app installed and billing through AppCoins`() {
    assertTrue(shouldShowShopTab(isInstalled = true, isAppCoins = true, isInCatappult = null))
    assertTrue(shouldShowShopTab(isInstalled = true, isAppCoins = false, isInCatappult = true))
    assertFalse(shouldShowShopTab(isInstalled = false, isAppCoins = true, isInCatappult = true))
    assertFalse(shouldShowShopTab(isInstalled = true, isAppCoins = false, isInCatappult = false))
    assertFalse(shouldShowShopTab(isInstalled = true, isAppCoins = false, isInCatappult = null))
  }

  @Test
  fun `the demo catalogue item comes first, then at most three cheap others`() {
    fun product(sku: String, micros: Long?) =
      ShopProduct(sku, "inapp", sku, null, "x", micros)
    val products = listOf(
      product("attempts07", 314_900_000),
      product("attempts01", 4_000_000),
      product("legendary_dice", 150_000),
      product("no_price", null),
      product("attempts", 1_000_000),
      product("gems", 2_000_000),
      product("coins", 3_000_000),
    )

    val ordered = AppShopRepository.orderForShop(products, "com.aptoide.diceroll.sdk.dev")

    assertEquals(
      listOf("attempts", "legendary_dice", "gems", "coins"),
      ordered.map { it.sku }
    )
  }
}
