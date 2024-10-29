package cm.aptoide.pt.installer.network

import androidx.annotation.Keep
import cm.aptoide.pt.feature_apps.data.model.Split
import com.google.gson.annotations.SerializedName

@Keep
data class DynamicSplitJSON(
  val name: String,
  val type: String,
  val md5sum: String,
  val path: String,
  val filesize: Long,
  @SerializedName(value = "delivery_types") val deliveryTypes: List<String>,
  val splits: List<Split>,
)
