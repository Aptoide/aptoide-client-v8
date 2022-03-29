package cm.aptoide.pt.feature_search.data.network.model

import cm.aptoide.pt.aptoide_network.data.network.model.AppCoins
import cm.aptoide.pt.aptoide_network.data.network.model.File
import cm.aptoide.pt.aptoide_network.data.network.model.Stats

data class SearchAppJsonList(
  val icon: String,
  val name: String, val file: File,
  val stats: Stats, val appcoins: AppCoins
)


