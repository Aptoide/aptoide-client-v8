package com.aptoide.android.aptoidegames.attribution.data

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class AttributionJSON(
  @SerializedName("package_name") val packageName: String? = null,
  @SerializedName("oemid") val oemId: String? = null,
  // Gson bypasses Kotlin null-safety, so an explicit JSON null still lands here as null.
  @SerializedName("guest_uid") val guestUid: String? = null,
  @SerializedName("utm_source") val utmSource: String? = null,
  @SerializedName("utm_medium") val utmMedium: String? = null,
  @SerializedName("utm_campaign") val utmCampaign: String? = null,
  @SerializedName("utm_term") val utmTerm: String? = null,
  @SerializedName("utm_content") val utmContent: String? = null,
)
