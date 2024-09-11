package cm.aptoide.pt.feature_apps.data

import cm.aptoide.pt.extensions.getRandomString
import cm.aptoide.pt.feature_apps.domain.Rating
import cm.aptoide.pt.feature_apps.domain.AppSource
import cm.aptoide.pt.feature_apps.domain.Store
import cm.aptoide.pt.feature_apps.domain.Votes
import cm.aptoide.pt.feature_campaigns.CampaignImpl
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random
import kotlin.random.nextLong

data class App(
  override val appId: Long,
  val name: String,
  override val packageName: String,
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
  val aab: Aab?,
  val obb: Obb?,
  val bdsFlags: List<String?>?,
  val developerName: String?,
  val campaigns: CampaignImpl? = null,
): AppSource

data class File(
  private val _fileName: String? = null,
  val vername: String,
  val vercode: Int,
  val md5: String,
  val filesize: Long,
  val path: String,
  val path_alt: String,
) {
  val fileName get() = _fileName ?: md5
}

data class Aab(
  val requiredSplitTypes: List<String>,
  val splits: List<Split>,
)

data class Split(
  val type: String,
  val file: File,
)

data class Obb(
  val main: File,
  val patch: File?,
)

val emptyApp = App(
  appId = 0,
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
  bdsFlags = null,
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
  aab = null,
  obb = null,
  developerName = ""
)

val randomApp
  get() =
    emptyApp.copy(
      appId = Random.nextLong(),
      name = getRandomString(range = 2..5, capitalize = true),
      packageName = getRandomString(range = 3..5, separator = "."),
      appSize = Random.nextLong(1_000_000L..1_000_000_000L),
      malware = if (Random.nextBoolean()) "trusted" else "fraud",
      rating = List(5) { i -> Votes(i, Random.nextInt(2000)) }.let {
        val totalVotes = it.map(Votes::count).sum().toLong()
        Rating(
          avgRating = it
            .mapIndexed { index, votes -> (index + 1.0) * votes.count }
            .sum() / totalVotes,
          totalVotes = totalVotes,
          votes = it
        )
      },
      pRating = List(5) { i -> Votes(i, Random.nextInt(2000)) }.let {
        val totalVotes = it.map(Votes::count).sum().toLong()
        val totalStars = it.mapIndexed { index, votes -> (index + 1.0) * votes.count }.sum()
        Rating(
          avgRating = totalStars / totalVotes,
          totalVotes = totalVotes,
          votes = it
        )
      },
      downloads = Random.nextInt(500_000),
      versionName = "${Random.nextInt(3)}.${Random.nextInt(20)}.${Random.nextInt(100)}",
      versionCode = Random.nextInt(),
      isAppCoins = Random.nextBoolean(),
      screenshots = List(Random.nextInt(10)) { "random" },
      description = getRandomString(),
      videos = List(Random.nextInt(2)) { "https://youtu.be/dQw4w9WgXcQ" },
      store = Store(
        storeName = getRandomString(range = 2..3, capitalize = true),
        icon = "",
        apps = Random.nextLong(100),
        subscribers = Random.nextLong(1000),
        downloads = Random.nextLong(500_000)
      ),
      releaseDate = LocalDateTime.now()
        .minusYears(Random.nextInt(9) - 1L)
        .format(DateTimeFormatter.ofPattern("uuuu-MM-dd hh:mm:ss")),
      updateDate = LocalDateTime.now()
        .minusDays(Random.nextLong(365))
        .format(DateTimeFormatter.ofPattern("uuuu-MM-dd hh:mm:ss")),
      releaseUpdateDate = LocalDate.now()
        .minusDays(Random.nextLong(365))
        .format(DateTimeFormatter.ofPattern("uuuu-MM-dd")),
      website = "www.${getRandomString(range = 1..3, separator = ".")}.com",
      email = "${getRandomString(range = 1..5, separator = "")}@email.com",
      permissions = listOf(
        "android.permission.ACCESS_COARSE_LOCATION",
        "android.permission.ACCESS_FINE_LOCATION",
        "android.permission.ACCESS_NETWORK_STATE",
        "android.permission.ACCESS_WIFI_STATE",
        "android.permission.BATTERY_STATS",
        "android.permission.BLUETOOTH",
        "android.permission.BLUETOOTH_CONNECT",
        "android.permission.CAMERA",
        "android.permission.CHANGE_NETWORK_STATE",
        "android.permission.CHANGE_WIFI_STATE",
        "android.permission.FOREGROUND_SERVICE",
        "android.permission.GET_ACCOUNTS",
        "android.permission.GET_PACKAGE_SIZE",
        "android.permission.INTERNET",
        "android.permission.MODIFY_AUDIO_SETTINGS",
        "android.permission.NETWORK",
        "android.permission.POST_NOTIFICATIONS",
        "android.permission.READ_CALENDAR",
        "android.permission.READ_MEDIA_IMAGES",
        "android.permission.READ_MEDIA_VIDEO",
        "android.permission.READ_PHONE_STATE",
        "android.permission.RECORD_AUDIO",
        "android.permission.REQUEST_INSTALL_PACKAGES",
        "android.permission.SET_WALLPAPER",
        "android.permission.USE_CREDENTIALS",
        "android.permission.VIBRATE",
        "android.permission.WAKE_LOCK",
        "android.permission.WRITE_CALENDAR",
        "android.permission.WRITE_EXTERNAL_STORAGE",
        "android.permission.WRITE_SETTINGS",
        "android.webkit.resource.AUDIO_CAPTURE",
        "android.webkit.resource.MIDI_SYSEX",
        "android.webkit.resource.PROTECTED_MEDIA_ID",
        "android.webkit.resource.VIDEO_CAPTURE",
        "com.adjust.preinstall.READ_PERMISSION",
        "com.android.vending.BILLING",
        "com.cleanmaster.mguard.permission.MTK_MESSAGE",
        "com.google.android.c2dm.permission.RECEIVE",
        "com.google.android.finsky.permission.BIND_GET_INSTALL_REFERRER_SERVICE",
        "com.google.android.gms.permission.AD_ID",
        "com.mobile.legends.permission.C2D_MESSAGE",
        "MediaStore.Images.Media.EXTERNAL_CONTENT_URI",
        "MediaStore.Images.Media.INTERNAL_CONTENT_URI",
        "org.onepf.openiab.permission.BILLING"
      ).shuffled().take(Random.nextInt(40) + 3),
      file = File(
        vername = "${Random.nextInt(3)}.${Random.nextInt(20)}.${Random.nextInt(100)}",
        vercode = Random.nextInt(),
        md5 = "md5",
        filesize = Random.nextLong(1_000_000L..1_000_000_000L),
        path = "",
        path_alt = ""
      ),
      developerName = getRandomString(range = 2..5, capitalize = true)
    )

fun App.isInCatappult(): Boolean? {
  return bdsFlags?.contains("STORE_BDS")
}

fun App.isAab(): Boolean = aab != null && aab.splits.isNotEmpty()

fun App.hasObb(): Boolean = (aab == null || aab.splits.isEmpty()) && obb != null
