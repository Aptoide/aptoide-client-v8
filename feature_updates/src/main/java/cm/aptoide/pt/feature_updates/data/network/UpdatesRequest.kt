package cm.aptoide.pt.feature_updates.data.network

import androidx.annotation.Keep
import cm.aptoide.pt.feature_updates.domain.ApkData
import com.google.gson.annotations.SerializedName

@Keep
data class UpdatesRequest(
  val cdn: String = "pool",
  val language: String = "en_US",
  val refresh: Boolean = false,
  @SerializedName("not_apk_tags") val notApkTags: String = "alpha,beta",
  @SerializedName("not_package_tags") val notPackageTags: String = "system",
  @SerializedName("apks_data") val apksData: List<ApkData>,
)
