package com.aptoide.android.aptoidegames.attribution.domain

import androidx.annotation.Keep

@Keep
data class AttributionModel(
  val packageName: String?,
  val oemId: String?,
  val guestUid: String,
  val utmSource: String?,
  val utmMedium: String?,
  val utmCampaign: String?,
  val utmTerm: String?,
  val utmContent: String?,
) {
  fun hasUTMs() =
    utmSource != null || utmMedium != null || utmCampaign != null ||
      utmTerm != null || utmContent != null
}
