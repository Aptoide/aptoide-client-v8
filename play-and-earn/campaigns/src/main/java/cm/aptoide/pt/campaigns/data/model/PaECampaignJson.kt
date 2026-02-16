package cm.aptoide.pt.campaigns.data.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
internal data class PaECampaignJson(
  @SerializedName("keep_playing") val keepPlayingCampaign: List<PaEAppJson>?,
  @SerializedName("trending") val trending: List<PaEAppJson>?
)

@Keep
internal data class PaEAppJson(
  @SerializedName("app_info") val appInfo: PaEAppInfoJson,
  val progress: PaEProgressJson?
)

@Keep
internal data class PaEAppInfoJson(
  @SerializedName("package") val packageName: String,
  val icon: String,
  val graphic: String,
  val name: String,
  val uname: String
)

@Keep
internal data class PaEProgressJson(
  val current: Int?,
  val target: Int,
  val type: String,
  val status: String?
)