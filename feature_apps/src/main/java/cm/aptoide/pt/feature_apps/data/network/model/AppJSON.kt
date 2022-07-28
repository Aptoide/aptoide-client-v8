package cm.aptoide.pt.feature_apps.data.network.model

import cm.aptoide.pt.aptoide_network.data.network.model.AppCoins
import cm.aptoide.pt.aptoide_network.data.network.model.File
import cm.aptoide.pt.aptoide_network.data.network.model.Screenshot
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
  var age: Age?,
  var developer: Developer?,
  var store: Store,
  var file: File,
  val media: Media?,
  var stats: cm.aptoide.pt.aptoide_network.data.network.model.Stats,
  var appcoins: AppCoins? = null,
  val obb: Obb?
)

data class Media(
  var keywords: List<String>,
  var description: String,
  var screenshots: List<Screenshot>
)

data class Store(
  var id: Long,
  var name: String,
  var avatar: String,
  var appearance: Appearance,
  var stats: Stats?
)

data class Stats(var apps: Long, var subscribers: Long, var downloads: Long)

data class Appearance(var theme: String, var description: String)

data class Developer(
  var id: Long,
  var name: String,
  var website: String,
  var email: String,
  var privacy: String?
)

data class Age(
  var id: Long,
  var name: String,
  var title: String,
  var pegi: String,
  var rating: Long
)

data class Obb(val main: Main, val patch: Patch?)

data class Main(val md5sum: String, val filesize: Long, val filename: String, val path: String)
data class Patch(val md5sum: String, val filesize: Long, val filename: String, val path: String)

