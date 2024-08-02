package cm.aptoide.pt.feature_apps.data.model

import androidx.annotation.Keep
import cm.aptoide.pt.aptoide_network.data.network.model.AppCoins
import cm.aptoide.pt.aptoide_network.data.network.model.File
import cm.aptoide.pt.aptoide_network.data.network.model.Screenshot
import com.google.gson.annotations.SerializedName

@Keep
data class AppJSON(
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
  var release: Release? = null,
  var mainPackage: String? = null,
  var age: Age? = null,
  var developer: Developer? = null,
  var store: Store,
  var file: File,
  val media: Media? = null,
  var stats: cm.aptoide.pt.aptoide_network.data.network.model.Stats,
  var appcoins: AppCoins? = null,
  val aab: Aab? = null,
  val obb: Obb? = null,
  val urls: CampaignUrls
)

@Keep
data class Media(
  var keywords: List<String>,
  var description: String,
  var videos: List<VideoJSON>,
  var screenshots: List<Screenshot>
)

@Keep
data class VideoJSON(
  val type: VideoTypeJSON,
  val url: String,
  val thumbnail: String
)

@Keep
@Suppress("unused")
enum class VideoTypeJSON {
  YOUTUBE
}

@Keep
data class Store(
  var id: Long,
  var name: String,
  var avatar: String,
  var appearance: Appearance,
  var stats: Stats?
)

@Keep
data class Stats(
  var apps: Long,
  var subscribers: Long,
  var downloads: Long
)

@Keep
data class Appearance(var theme: String, var description: String)

@Keep
data class Developer(
  var id: Long,
  var name: String,
  var website: String,
  var email: String,
  var privacy: String?
)

@Keep
data class Release(
  var updated: String,
)

@Keep
data class Age(
  var id: Long,
  var name: String,
  var title: String,
  var pegi: String,
  var rating: Long
)

@Keep
data class Aab(
  @SerializedName(value = "required_split_types") val requiredSplitTypes: List<String>,
  val splits: List<Split>,
)

@Keep
data class Split(
  val name: String,
  val type: String,
  val md5sum: String,
  val path: String,
  val filesize: Long,
)

@Keep
data class Obb(val main: Main, val patch: Patch?)

@Keep
data class Main(
  val md5sum: String,
  val filesize: Long,
  val filename: String,
  val path: String?
)

@Keep
data class Patch(
  val md5sum: String,
  val filesize: Long,
  val filename: String,
  val path: String?
)

@Keep
data class CampaignUrls(
  val impression: List<CampaignUrl>?,
  val click: List<CampaignUrl>?
)

@Keep
data class CampaignUrl(
  val name: String,
  val url: String
)
