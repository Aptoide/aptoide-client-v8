package cm.aptoide.pt.feature_apps.data

import cm.aptoide.pt.feature_apps.domain.Rating
import cm.aptoide.pt.feature_apps.domain.Store
import cm.aptoide.pt.feature_campaigns.CampaignImpl

data class App(
  val name: String,
  val packageName: String,
  val md5: String,
  val appSize: Long,
  val icon: String,
  val malware: String?,
  val rating: Rating,
  val pRating: Rating,
  val downloads: Int,
  val versionName: String,
  val versionCode: Int,
  val featureGraphic: String,
  val isAppCoins: Boolean,
  val screenshots: List<String>?,
  val description: String?,
  val youtubeVideos: List<String> = emptyList(),
  val store: Store,
  val releaseDate: String?,
  val releaseUpdateDate: String? = null,
  val updateDate: String?,
  val website: String?,
  val email: String?,
  val privacyPolicy: String?,
  val permissions: List<String>?,
  val file: File,
  val obb: Obb?,
  val developerName: String?,
  val campaigns: CampaignImpl? = null
)

data class File(
  var vername: String,
  var vercode: Int,
  var md5: String,
  var filesize: Long,
  var path: String?,
  var path_alt: String?
)

data class Obb(val main: File, val patch: File?)

val emptyApp = App(
  name = "",
  packageName = "",
  md5 = "",
  appSize = 0,
  icon = "",
  malware = "",
  rating = Rating(
    avgRating = 0.0,
    totalVotes = 0,
    votes = emptyList()
  ),
  pRating = Rating(
    avgRating = 0.0,
    totalVotes = 0,
    votes = emptyList()
  ),
  downloads = 0,
  versionName = "",
  versionCode = 0,
  featureGraphic = "",
  isAppCoins = false,
  screenshots = emptyList(),
  description = "",
  youtubeVideos = emptyList(),
  store = Store(
    storeName = "",
    icon = "",
    apps = null,
    subscribers = null,
    downloads = null
  ),
  releaseDate = "",
  updateDate = "",
  website = "",
  email = "",
  privacyPolicy = "",
  permissions = emptyList(),
  file = File(
    vername = "",
    vercode = 0,
    md5 = "",
    filesize = 0,
    path = "",
    path_alt = ""
  ),
  obb = null,
  developerName = ""
)
