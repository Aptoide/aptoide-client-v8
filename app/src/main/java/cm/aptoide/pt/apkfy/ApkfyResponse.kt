package cm.aptoide.pt.apkfy

import android.util.Log
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ApkfyResponse(
  @SerializedName("guest_uid") val guestUId: String = "",
  @SerializedName("package_name") val packageName: String? = null,
  @SerializedName("app_id") val appId: Long? = null,
  @SerializedName("utm_source") val utmSource: String? = null,
  @SerializedName("utm_medium") val utmMedium: String? = null,
  @SerializedName("utm_campaign") val utmCampaign: String? = null,
  @SerializedName("utm_term") val utmTerm: String? = null,
  @SerializedName("utm_content") val utmContent: String? = null,
  @SerializedName("oemid") val oemid: String? = null
)

fun ApkfyResponse.mapToApkfyModel(): ApkfyModel {
  return ApkfyModel(
    packageName = packageName,
    appId = appId,
    oemId = oemid,
    guestUid = guestUId,
    utmSource = utmSource,
    utmMedium = utmMedium,
    utmCampaign = utmCampaign,
    utmTerm = utmTerm,
    utmContent = utmContent
  )
}
