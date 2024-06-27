package cm.aptoide.pt.apkfy

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ApkfyResponse(
  @SerializedName("guest_uid") val guestUId: String = "",
  @SerializedName("package_name") val packageName: String? = null,
  @SerializedName("app_id") val appId: Long? = null,
  @SerializedName("oemid") val oemId: String? = null
)

fun ApkfyResponse.mapToApkfyModel(): ApkfyModel {
  return ApkfyModel(packageName, appId, oemId, guestUId)
}
