package com.aptoide.android.aptoidegames.gamegenie.purchase

import java.text.Normalizer

/**
 * HACKATHON: client-side purchase intent detection for the Genie chat. A message is a purchase
 * request when it has a buy verb and names a catalogue game (case and diacritic insensitive).
 */
object PurchaseIntentResolver {

  private val buyVerbs = setOf("buy", "purchase", "get", "comprar", "compra", "quero")
  private val diacritics = Regex("\\p{Mn}+")
  private val nonAlphanumeric = Regex("[^a-z0-9]+")

  fun resolve(
    userText: String,
    catalog: List<GeniePurchaseItem> = GeniePurchaseCatalog.items,
  ): GeniePurchaseItem? {
    val normalized = normalize(userText)
    if (normalized.isBlank()) return null
    val words = normalized.split(' ').toSet()
    if (words.none { it in buyVerbs }) return null
    val padded = " $normalized "
    return catalog.firstOrNull { item ->
      item.aliases.any { alias -> padded.contains(" ${normalize(alias)} ") }
    }
  }

  private fun normalize(text: String): String =
    Normalizer.normalize(text.lowercase(), Normalizer.Form.NFD)
      .replace(diacritics, "")
      .replace(nonAlphanumeric, " ")
      .trim()
}
