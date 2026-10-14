package com.aptoide.android.aptoidegames.play_and_earn.domain.mmp

import java.net.URLEncoder

/** The values Aptoide puts in a developer's MMP click link (AND-877). */
data class PaEMmpClickParams(
  val wallet: String,
  val clickId: String,
  val advertisingId: String?,
  val campaignId: String,
  val channel: String,
)

/**
 * Fills a developer's AppsFlyer click link, built from our `aptoide_int` partner template.
 *
 * AppsFlyer echoes these values back in the install and in-app event postbacks, which is how the
 * backend links a postback to a user: the wallet travels in `af_sub1`.
 */
object PaEMmpClickUrlBuilder {

  private const val WALLET_PARAM = "af_sub1"
  private val placeholder = Regex("\\{[^}]*\\}")

  /** Returns the click URL, or null when the template can't be sent safely. */
  fun build(template: String, params: PaEMmpClickParams): String? {
    // The link carries the wallet and the advertising id: never over plain http.
    if (!template.startsWith("https://")) return null

    val values = mapOf(
      "{transaction_id}" to params.clickId,
      "{google_aid}" to params.advertisingId?.takeUnless { it.isMissingAdvertisingId() },
      "{affiliate_id}" to params.channel,
      "{offer_id}" to params.campaignId,
      "{offer_ref_id}" to params.campaignId,
    )

    val fragment = template.substringAfter('#', missingDelimiterValue = "")
    val withoutFragment = template.substringBefore('#')
    val base = withoutFragment.substringBefore('?')
    // Only query values are filled: a placeholder anywhere else would reach the MMP as is.
    if (placeholder.containsMatchIn(base)) return null

    val query = withoutFragment.substringAfter('?', missingDelimiterValue = "")
      .split('&')
      .filter { it.isNotEmpty() }
      .mapNotNull { param -> param.fill(values) }
      .filterNot { it.substringBefore('=') == WALLET_PARAM } +
      "$WALLET_PARAM=${params.wallet.encoded()}"

    return "$base?${query.joinToString("&")}" + if (fragment.isEmpty()) "" else "#$fragment"
  }

  /** Returns the parameter with its known placeholders filled, or null to drop it. */
  private fun String.fill(values: Map<String, String?>): String? {
    var filled = this
    values.forEach { (key, value) ->
      if (filled.contains(key)) {
        filled = filled.replace(key, value?.encoded() ?: return null)
      }
    }
    // A literal placeholder must never reach the MMP.
    return filled.takeUnless { placeholder.containsMatchIn(it) }
  }

  private fun String.encoded(): String = URLEncoder.encode(this, "UTF-8")

  // Users who reset or delete the advertising id get all zeros.
  private fun String.isMissingAdvertisingId() = isBlank() || all { it == '0' || it == '-' }
}
