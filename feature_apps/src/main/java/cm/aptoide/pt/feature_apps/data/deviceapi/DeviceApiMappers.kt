package cm.aptoide.pt.feature_apps.data.deviceapi

import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.File
import cm.aptoide.pt.feature_apps.data.deviceapi.model.AppSummaryResponse
import cm.aptoide.pt.feature_apps.data.deviceapi.model.RatingResponse
import cm.aptoide.pt.feature_apps.domain.AppOrigin
import cm.aptoide.pt.feature_apps.domain.Rating
import cm.aptoide.pt.feature_apps.domain.Store
import cm.aptoide.pt.feature_apps.domain.Votes
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

/**
 * A device API summary as an [App], for cards and lists. It holds what identifies and shows
 * the app and nothing to install it with - its details are fetched when it is opened.
 *
 * What the device API does not send is left empty rather than made up: no v7 id, so the app
 * is addressed by its package; no store flags; no campaigns. Null when there is no package
 * name, as nothing could be done with such an app.
 *
 * [storeName] is the store this build reads, which is what install analytics report.
 */
fun AppSummaryResponse.toApp(storeName: String): App? {
  val packageName = packageName?.takeIf { it.isNotBlank() } ?: return null
  val updateDate = updatedAt?.toV7Date()
  val downloads = downloads.toDownloads()
  val rating = rating.toRating()
  return App(
    appId = 0L,
    name = name.orEmpty(),
    packageName = packageName,
    md5 = "",
    icon = iconUrl.orEmpty(),
    malware = null,
    rating = rating,
    pRating = rating,
    downloads = downloads,
    pDownloads = downloads,
    versionName = "",
    versionCode = 0,
    featureGraphic = featureGraphicUrl.orEmpty(),
    isAppCoins = aptoideBilling == true,
    screenshots = emptyList(),
    description = null,
    news = null,
    videos = emptyList(),
    store = Store(
      storeName = storeName,
      icon = "",
      apps = null,
      subscribers = null,
      downloads = null
    ),
    releaseDate = null,
    modifiedDate = updateDate.orEmpty(),
    updateDate = updateDate,
    website = null,
    email = null,
    privacyPolicy = null,
    permissions = null,
    file = File(md5 = "", size = 0, path = "", path_alt = ""),
    aab = null,
    obb = null,
    bdsFlags = null,
    developerName = null,
    campaigns = null,
    signature = null,
    origin = AppOrigin.DEVICE_API,
  )
}

internal fun RatingResponse?.toRating(): Rating = Rating(
  avgRating = this?.average ?: 0.0,
  totalVotes = this?.count ?: 0,
  votes = this?.distribution?.mapNotNull { bucket ->
    bucket.stars?.let { Votes(value = it, count = bucket.count ?: 0) }
  },
)

// The app model counts downloads in an Int, the device API in totals that do not fit one
internal fun Long?.toDownloads(): Int = (this ?: 0L).coerceIn(0L, Int.MAX_VALUE.toLong()).toInt()

private val v7DateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

// The device API sends ISO-8601 instants, the app reads dates in the v7 format
internal fun String.toV7Date(): String? = runCatching {
  OffsetDateTime.parse(this).withOffsetSameInstant(ZoneOffset.UTC).format(v7DateFormat)
}.getOrNull()
