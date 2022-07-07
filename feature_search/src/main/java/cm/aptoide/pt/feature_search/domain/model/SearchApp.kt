package cm.aptoide.pt.feature_search.domain.model

data class SearchApp(
  val appName: String,
  val packageName: String,
  val icon: String,
  val rating: Double,
  val downloads: Int,
  val malware: String?
)