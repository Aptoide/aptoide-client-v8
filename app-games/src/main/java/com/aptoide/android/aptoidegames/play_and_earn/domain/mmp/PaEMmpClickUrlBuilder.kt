package com.aptoide.android.aptoidegames.play_and_earn.domain.mmp

import cm.aptoide.pt.campaigns.domain.PaEMmpPlatform
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
 * Fills a developer's MMP click link, built from our partner template on AppsFlyer
 * (`aptoide_int`) or Adjust (AND-912).
 *
 * The MMP echoes these values back in the install and in-app event postbacks, which is how the
 * backend links a postback to a user: the wallet travels in the one custom parameter each MMP
 * gives a partner, `af_sub1` on AppsFlyer and `label` on Adjust. The macros are the union of
 * both platforms' names; an unknown one is dropped, so no placeholder reaches the MMP as text.
 */
object PaEMmpClickUrlBuilder {

  private val placeholder = Regex("\\{[^}]*\\}")

  /** Returns the click URL, or null when the template can't be sent safely. */
  fun build(template: String, params: PaEMmpClickParams, mmp: PaEMmpPlatform): String? {
    // The link carries the wallet and the advertising id: never over plain http.
    if (!template.startsWith("https://")) return null

    val advertisingId = params.advertisingId?.takeUnless { it.isMissingAdvertisingId() }
    val values = mapOf(
      // AppsFlyer
      "{transaction_id}" to params.clickId,
      "{google_aid}" to advertisingId,
      "{affiliate_id}" to params.channel,
      "{offer_id}" to params.campaignId,
      "{offer_ref_id}" to params.campaignId,
      // Adjust
      "{click_id}" to params.clickId,
      "{gps_adid}" to advertisingId,
    )
    val walletParam = mmp.walletParam()

    val fragment = template.substringAfter('#', missingDelimiterValue = "")
    val withoutFragment = template.substringBefore('#')
    val base = withoutFragment.substringBefore('?')
    // Only query values are filled: a placeholder anywhere else would reach the MMP as is.
    if (placeholder.containsMatchIn(base)) return null

    val query = withoutFragment.substringAfter('?', missingDelimiterValue = "")
      .split('&')
      .filter { it.isNotEmpty() }
      .mapNotNull { param -> param.fill(values) }
      .filterNot { it.substringBefore('=') == walletParam } +
      "$walletParam=${params.wallet.encoded()}"

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

  /** The link parameter the MMP echoes back in its postbacks, where the wallet goes. */
  private fun PaEMmpPlatform.walletParam(): String = when (this) {
    PaEMmpPlatform.APPSFLYER -> "af_sub1"
    PaEMmpPlatform.ADJUST -> "label"
  }

  private fun String.encoded(): String = URLEncoder.encode(this, "UTF-8")

  // Users who reset or delete the advertising id get all zeros.
  private fun String.isMissingAdvertisingId() = isBlank() || all { it == '0' || it == '-' }
}
