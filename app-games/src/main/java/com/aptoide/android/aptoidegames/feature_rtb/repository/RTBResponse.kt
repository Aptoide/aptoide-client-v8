package com.aptoide.android.aptoidegames.feature_rtb.repository

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class RTBResponse(
  @SerializedName("ad_type") val adType: String,
  @SerializedName("campaign_id") val campaignId: String,
  @SerializedName("package_name") val packageName: String,
  @SerializedName("app_name") val appName: String,
  val rating: Double,
  @SerializedName("billing_provider") val billingProvider: String?,
  @SerializedName("install_from") val installFrom: String?,
  @SerializedName("cta_text") val ctaText: String,
  @SerializedName("ad_domain") val adDomain: String,
  val creative: Creative,
  val tracking: Tracking
)

@Keep
data class Creative(
  val asset: String,
  val width: Int,
  val height: Int
)

@Keep
data class Tracking(
  @SerializedName("aptoide-mmp")
  val aptoideMmp: AptoideMmp?,
  @SerializedName("adsnetwork")
  val adsNetwork: AdsNetwork?,
)

@Keep
data class AptoideMmp(
  val impression: String?,
  val click: String?,
  val download: String?
)

@Keep
data class AdsNetwork(
  val click: String?,
  val timeout: Int?,
)
