package cm.aptoide.pt.feature_apps.data.model

import androidx.annotation.Keep
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
