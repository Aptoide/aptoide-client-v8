package cm.aptoide.pt.feature_apps.data

import cm.aptoide.pt.feature_apps.domain.Store

data class App(
  val name: String,
  val icon: String,
  val malware: String,
  val rating: Double,
  val downloads: Int,
  val versionName: String,
  val featureGraphic: String,
  val isAppCoins: Boolean,
  val screenshots: List<String>, val description: String, val store: Store
)
