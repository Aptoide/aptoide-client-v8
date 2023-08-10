package cm.aptoide.pt.feature_apps.data

import cm.aptoide.pt.feature_apps.domain.Rating
import cm.aptoide.pt.feature_apps.domain.Store
import cm.aptoide.pt.feature_apps.domain.Votes
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
  val videos: List<String> = emptyList(),
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
  val campaigns: CampaignImpl? = null,
)

data class File(
  private val _fileName: String? = null,
  val vername: String,
  val vercode: Int,
  val md5: String,
  val filesize: Long,
  val path: String?,
  val path_alt: String?,
) {
  val fileName get() = _fileName ?: md5
}

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
  videos = emptyList(),
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

val mockApp = App(
  name = "Lords Mobile: Kingdom Wars",
  packageName = "teste",
  md5 = "md5",
  appSize = 123,
  icon = "teste",
  malware = "trusted",
  rating = Rating(
    avgRating = 2.3,
    totalVotes = 12321,
    votes = listOf(
      Votes(1, 3),
      Votes(2, 8),
      Votes(3, 123),
      Votes(4, 100),
      Votes(5, 1994)
    )
  ),
  pRating = Rating(
    avgRating = 2.3,
    totalVotes = 12321,
    votes = listOf(
      Votes(1, 3),
      Votes(2, 8),
      Votes(3, 123),
      Votes(4, 100),
      Votes(5, 1994)
    )
  ),
  downloads = 123,
  versionName = "teste",
  versionCode = 123,
  featureGraphic = "",
  isAppCoins = true,
  screenshots = listOf("dasdsa", "dsadas"),
  description = "App description",
  videos = listOf("", ""),
  store = Store(
    storeName = "rmota",
    icon = "rmota url",
    apps = 123,
    subscribers = 12313,
    downloads = 123123123123
  ),
  releaseDate = "13123",
  updateDate = "12313",
  website = "aptoide.com",
  email = "aptoide@aptoide.com",
  privacyPolicy = "none",
  permissions = listOf("permission 1", "permission 2"),
  file = File(
    vername = "asdas",
    vercode = 123,
    md5 = "md5",
    filesize = 123,
    path = null,
    path_alt = null
  ),
  obb = null,
  developerName = null
)
