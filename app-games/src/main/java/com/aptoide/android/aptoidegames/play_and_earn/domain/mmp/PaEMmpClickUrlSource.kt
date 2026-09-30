package com.aptoide.android.aptoidegames.play_and_earn.domain.mmp

import javax.inject.Inject

/** A developer's MMP click link and the P&E campaign it belongs to. */
data class PaEMmpClickTemplate(
  val url: String,
  val campaignId: String,
)

/** Where the MMP click link of a Play & Earn game comes from. */
interface PaEMmpClickUrlSource {
  suspend fun clickTemplate(packageName: String): PaEMmpClickTemplate?
}

/**
 * Temporary source until the campaign's `mmp_click_url` is served by `/missions`: every Play & Earn
 * game uses the Dice Roll test link.
 */
class HardcodedPaEMmpClickUrlSource @Inject constructor() : PaEMmpClickUrlSource {

  override suspend fun clickTemplate(packageName: String) = PaEMmpClickTemplate(
    url = DICE_ROLL_CLICK_URL,
    campaignId = DICE_ROLL_CAMPAIGN_ID,
  )

  private companion object {
    const val DICE_ROLL_CLICK_URL = "https://app.appsflyer.com/com.aptoide.diceroll.sdk-Aptoide" +
      "?pid=aptoide_int&af_siteid={affiliate_id}&c={offer_ref_id}&af_c_id={offer_id}" +
      "&af_click_lookback=7d&clickid={transaction_id}&advertising_id={google_aid}&idfa={ios_ifa}"
    const val DICE_ROLL_CAMPAIGN_ID = "pae_diceroll_test"
  }
}
