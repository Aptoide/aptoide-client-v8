package cm.aptoide.pt.feature_apps.data.network.model

import com.google.gson.annotations.SerializedName

internal data class AppJSON(
  var id: Long? = null,
  var name: String? = null,
  @SerializedName(value = "package") var packageName: String? = null,
  var uname: String? = null,
  var size: Long? = null,
  var icon: String? = null,
  var graphic: String? = null,
  var added: String? = null,
  var modified: String? = null,
  var updated: String? = null,
  var mainPackage: String? = null,
  var appcoins: AppCoinsJSON? = null,
)

internal data class AppCoinsJSON(
  var advertising: Boolean,
  var billing: Boolean,
  var flags: Boolean,
)
