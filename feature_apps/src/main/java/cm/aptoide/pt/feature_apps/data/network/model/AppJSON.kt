package cm.aptoide.pt.feature_apps.data.network.model

internal data class AppJSON(
  var name: String? = null,
  var icon: String? = null,
  var graphic: String? = null,
  var appcoins: AppCoinsJSON? = null,
)

internal data class AppCoinsJSON(
  var advertising: Boolean,
  var billing: Boolean,
  var flags: Boolean,
)
