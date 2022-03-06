package cm.aptoide.pt.feature_search.data.network.model

data class SearchAppJsonList(
  val icon: String,
  val name: String, val file: File,
  val stats: Stats, val appcoins: AppCoins
)

data class File(val malware: Malware)
data class Malware(
  val rank: String
)

data class Stats(val downloads: Int, val pdownloads: Int, val rating: Rating, val prating: Rating)

data class Rating(val avg: Double, val total: Double)

data class AppCoins(val advertising: Boolean, val billing: Boolean)
