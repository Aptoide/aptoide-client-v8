package cm.aptoide.pt.feature_search.data.network.model

import cm.aptoide.pt.aptoide_network.data.network.AppCoins
import cm.aptoide.pt.aptoide_network.data.network.File
import cm.aptoide.pt.aptoide_network.data.network.Stats

data class SearchAppJsonList(
  val icon: String,
  val name: String, val file: File,
  val stats: Stats, val appcoins: AppCoins
)


