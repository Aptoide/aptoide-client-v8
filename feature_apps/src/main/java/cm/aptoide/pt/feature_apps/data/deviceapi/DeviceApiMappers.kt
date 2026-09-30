package cm.aptoide.pt.feature_apps.data.deviceapi

import cm.aptoide.pt.aptoide_network.data.network.model.Screenshot
import cm.aptoide.pt.feature_apps.data.Aab
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.File
import cm.aptoide.pt.feature_apps.data.Obb
import cm.aptoide.pt.feature_apps.data.Split
import cm.aptoide.pt.feature_apps.data.deviceapi.model.AppResponse
import cm.aptoide.pt.feature_apps.data.deviceapi.model.AppSummaryResponse
import cm.aptoide.pt.feature_apps.data.deviceapi.model.ArtifactResponse
import cm.aptoide.pt.feature_apps.data.deviceapi.model.RatingResponse
import cm.aptoide.pt.feature_apps.data.deviceapi.model.ScreenshotResponse
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

/**
 * A device API detail as an [App], with everything the app view shows and, when the release
 * has an apk artifact, what the Aptoide installer needs. Under the Play catalog the release
 * is empty, and so are version, size and file.
 *
 * As with a summary, what the device API does not send is left empty, and there is no app
 * without a package name.
 */
fun AppResponse.toApp(storeName: String): App? {
  val packageName = packageName?.takeIf { it.isNotBlank() } ?: return null
  val releaseDate = release?.releasedAt?.toV7Date()
  val downloads = downloads.toDownloads()
  val rating = rating.toRating()
  val artifacts = release?.artifacts.orEmpty()
  val apk = artifacts.firstOrNull { it.kind == ARTIFACT_APK }
  return App(
    appId = 0L,
    name = name.orEmpty(),
    packageName = packageName,
    md5 = apk?.md5.orEmpty(),
    icon = iconUrl.orEmpty(),
    malware = null,
    rating = rating,
    pRating = rating,
    downloads = downloads,
    pDownloads = downloads,
    versionName = release?.versionName.orEmpty(),
    versionCode = release?.versionCode ?: 0,
    featureGraphic = featureGraphicUrl.orEmpty(),
    isAppCoins = aptoideBilling == true,
    screenshots = screenshots.orEmpty().mapNotNull { it.toScreenshot() },
    description = description,
    news = whatsNew,
    videos = videos.orEmpty().mapNotNull { it.url?.takeIf { url -> url.isNotBlank() } },
    store = Store(
      storeName = storeName,
      icon = "",
      apps = null,
      subscribers = null,
      downloads = null
    ),
    releaseDate = releaseDate,
    modifiedDate = releaseDate.orEmpty(),
    releaseUpdateDate = releaseDate,
    updateDate = releaseDate,
    website = developerWebsite,
    email = developerEmail,
    privacyPolicy = privacyPolicyUrl,
    // An empty list reads as unknown, which the app view hides
    permissions = release?.permissions?.takeIf { it.isNotEmpty() },
    file = apk.toFile(),
    aab = artifacts.toAab(),
    obb = artifacts.toObb(),
    bdsFlags = null,
    developerName = publisherName,
    campaigns = null,
    signature = null,
    origin = AppOrigin.DEVICE_API,
  )
}

private const val ARTIFACT_APK = "apk"
private const val ARTIFACT_SPLIT = "split"
private const val ARTIFACT_OBB_MAIN = "obb_main"
private const val ARTIFACT_OBB_PATCH = "obb_patch"

// The app view lays a screenshot out by its shape, which a missing or zero dimension cannot
// give; the Play catalog sends none, so those get a landscape shape
private const val DEFAULT_SCREENSHOT_WIDTH = 1920
private const val DEFAULT_SCREENSHOT_HEIGHT = 1080

private fun ScreenshotResponse.toScreenshot(): Screenshot? {
  val url = url?.takeIf { it.isNotBlank() } ?: return null
  val width = width ?: 0
  val height = height ?: 0
  return if (width > 0 && height > 0) {
    Screenshot(url = url, height = height, width = width)
  } else {
    Screenshot(url = url, height = DEFAULT_SCREENSHOT_HEIGHT, width = DEFAULT_SCREENSHOT_WIDTH)
  }
}

private fun ArtifactResponse?.toFile(): File = File(
  md5 = this?.md5.orEmpty(),
  size = this?.sizeBytes ?: 0,
  path = this?.url.orEmpty(),
  path_alt = "",
)

private fun List<ArtifactResponse>.toAab(): Aab? {
  val splits = filter { it.kind == ARTIFACT_SPLIT }
  if (splits.isEmpty()) return null
  return Aab(
    requiredSplitTypes = emptyList(),
    baseSplits = splits.map { Split(type = it.filename ?: ARTIFACT_SPLIT, file = it.toFile()) },
  )
}

private fun List<ArtifactResponse>.toObb(): Obb? {
  val main = firstOrNull { it.kind == ARTIFACT_OBB_MAIN } ?: return null
  val patch = firstOrNull { it.kind == ARTIFACT_OBB_PATCH }
  return Obb(main = main.toFile(), patch = patch?.toFile())
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
