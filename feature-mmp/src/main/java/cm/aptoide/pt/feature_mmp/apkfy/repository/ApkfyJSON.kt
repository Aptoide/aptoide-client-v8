package cm.aptoide.pt.feature_mmp.apkfy.repository

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ApkfyJSON(
  @SerializedName("guest_uid") val guestUId: String = "",
  @SerializedName("package_name") val packageName: String? = null,
  @SerializedName("app_id") val appId: Long? = null,
  @SerializedName("oemid") val oemId: String? = null,
  @SerializedName("utm_source") val utmSource: String? = null,
  @SerializedName("utm_medium") val utmMedium: String? = null,
  @SerializedName("utm_campaign") val utmCampaign: String? = null,
  @SerializedName("utm_term") val utmTerm: String? = null,
  @SerializedName("utm_content") val utmContent: String? = null,
)
