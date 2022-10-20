package cm.aptoide.pt.feature_search.data.network.model

import androidx.annotation.Keep
import cm.aptoide.pt.aptoide_network.data.network.model.AppCoins
import cm.aptoide.pt.aptoide_network.data.network.model.File
import cm.aptoide.pt.aptoide_network.data.network.model.Stats
import com.google.gson.annotations.SerializedName

@Keep
data class SearchAppJsonList(
  val icon: String,
  val name: String,
  @SerializedName(value = "package") var packageName: String,
  val file: File,
  val stats: Stats,
  val appcoins: AppCoins
)


