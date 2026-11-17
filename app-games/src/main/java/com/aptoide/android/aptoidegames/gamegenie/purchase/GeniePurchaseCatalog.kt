package com.aptoide.android.aptoidegames.gamegenie.purchase

// =================================================================================================
// HACKATHON MOCK: hardcoded demo catalogue. There is no catalogue/SKU service behind the Genie
// purchase flow; title and price are display-only (the purchase link never carries the price).
// =================================================================================================

data class GeniePurchaseItem(
  val gameName: String,
  /** Lower-case, diacritic-free names the user may type. */
  val aliases: List<String>,
  /** Tried in order; the first installed one is used. */
  val packageCandidates: List<String>,
  val sku: String,
  val type: String,
  val title: String,
  val price: String,
)

object GeniePurchaseCatalog {

  val diceroll = GeniePurchaseItem(
    gameName = "Diceroll",
    aliases = listOf("diceroll", "dice roll"),
    // Aptoide/aptoide-diceroll-sdk (debug, then release), then the older Catappult sample.
    packageCandidates = listOf(
      "com.aptoide.diceroll.sdk.dev",
      "com.aptoide.diceroll.sdk",
      "com.appcoins.diceroll.sdk.dev",
      "com.appcoins.diceroll.sdk",
    ),
    sku = "attempts",
    type = "inapp",
    title = "Extra attempts",
    price = "€1.00",
  )

  // Other Diceroll dev packs, reachable from the ag://genie/buy link (outside assistants, perk
  // pushes). A typed "buy ... in Diceroll" still resolves to the first item.
  val dicerollLegendaryDice =
    diceroll.copy(sku = "legendary_dice", title = "Legendary Dice", price = "€0.15")
  val dicerollAttemptsPack =
    diceroll.copy(sku = "attempts01", title = "Attempts pack", price = "€4.00")

  val items: List<GeniePurchaseItem> =
    listOf(diceroll, dicerollLegendaryDice, dicerollAttemptsPack)
}
