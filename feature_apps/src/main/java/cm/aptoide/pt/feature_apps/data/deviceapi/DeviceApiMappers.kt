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
import cm.aptoide.pt.feature_apps.data.deviceapi.model.ReleaseResponse
import cm.aptoide.pt.feature_apps.data.deviceapi.model.TrustResponse
import cm.aptoide.pt.feature_apps.domain.Rating
import cm.aptoide.pt.feature_apps.domain.Store
import cm.aptoide.pt.feature_apps.domain.Trust
import cm.aptoide.pt.feature_apps.domain.Votes

/**
 * Device-API → domain `App` mapping (aptoideGamesDev path). Maps the new contract
 * cleanly and synthesizes the handful of v7-only fields the domain model still
 * carries (`appId`=0 so `asSource()` uses the package; `store` placeholder;
 * `isAppCoins`=false — billing stays on v7; `malware`/`signature` null, superseded
 * by the structured `trust`). See `.context/contract-friction.md`.
 */

/** Full app detail. `hasMeta` stays false here — `AppMetaUseCase` flips it, as on v7. */
fun AppResponse.toApp(storeName: String): App {
  val artifacts = release?.artifacts.orEmpty()
  val apk = artifacts.firstOrNull { it.kind == "apk" }
  return App(
    appId = 0L,
    name = name.orEmpty(),
    packageName = packageName.orEmpty(),
    md5 = apk?.md5.orEmpty(),
    icon = iconUrl.orEmpty(),
    malware = null,
    rating = rating.toRating(),
    pRating = rating.toRating(),
    downloads = downloads ?: 0,
    // The UI renders pDownloads. Use the source `downloads` (total across sources,
    // e.g. 10M) for a meaningful + card-consistent number, NOT `aptoide_downloads`
    // (Aptoide's own metric, ~1 on dev until the nightly rollup — APT-94). Which
    // number to surface is a product choice; revisit if we want the Aptoide-only count.
    pDownloads = downloads ?: 0,
    versionName = release?.versionName.orEmpty(),
    versionCode = release?.versionCode ?: 0,
    featureGraphic = featureGraphicUrl.orEmpty(),
    isAppCoins = false,
    screenshots = screenshots?.mapNotNull { it.toScreenshot() },
    description = description,
    news = null,
    videos = videos?.mapNotNull { it.url } ?: emptyList(),
    store = deviceStore(storeName),
    releaseDate = release?.releasedAt,
    modifiedDate = release?.releasedAt.orEmpty(),
    releaseUpdateDate = release?.releasedAt,
    updateDate = release?.releasedAt,
    website = null,
    email = null,
    privacyPolicy = null,
    permissions = null,
    file = apk.toFile(),
    aab = artifacts.toAab(),
    obb = artifacts.toObb(),
    bdsFlags = null,
    developerName = publisherName,
    signature = null,
    trust = trust.toTrust(),
  )
}

/** Thin summary (cards/lists). Tapping through re-fetches full detail via `getApp`. */
fun AppSummaryResponse.toApp(storeName: String): App = App(
  appId = 0L,
  name = name.orEmpty(),
  packageName = packageName.orEmpty(),
  md5 = "",
  icon = iconUrl.orEmpty(),
  malware = null,
  rating = rating.toRating(),
  pRating = rating.toRating(),
  downloads = downloads ?: 0,
  // UI shows pDownloads; summaries have no aptoide_downloads → use source downloads.
  pDownloads = downloads ?: 0,
  versionName = "",
  versionCode = 0,
  featureGraphic = featureGraphicUrl.orEmpty(),
  isAppCoins = false,
  screenshots = emptyList(),
  description = null,
  news = null,
  videos = emptyList(),
  store = deviceStore(storeName),
  releaseDate = null,
  modifiedDate = "",
  updateDate = null,
  website = null,
  email = null,
  privacyPolicy = null,
  permissions = null,
  file = File(md5 = "", size = 0, path = "", path_alt = ""),
  aab = null,
  obb = null,
  bdsFlags = null,
  developerName = null,
  signature = null,
  trust = null,
)

/** A per-version [App] from a release-history entry, merged with base app identity. */
fun ReleaseResponse.toVersionApp(base: App, trust: TrustResponse?): App {
  val artifacts = artifacts.orEmpty()
  val apk = artifacts.firstOrNull { it.kind == "apk" }
  return base.copy(
    md5 = apk?.md5.orEmpty(),
    versionName = versionName.orEmpty(),
    versionCode = versionCode ?: 0,
    releaseDate = releasedAt,
    modifiedDate = releasedAt.orEmpty(),
    updateDate = releasedAt,
    releaseUpdateDate = releasedAt,
    file = apk.toFile(),
    aab = artifacts.toAab(),
    obb = artifacts.toObb(),
    trust = trust.toTrust(),
  )
}

private fun deviceStore(storeName: String) =
  Store(storeName = storeName, icon = "", apps = null, subscribers = null, downloads = null)

private fun RatingResponse?.toRating(): Rating = Rating(
  avgRating = this?.average ?: 0.0,
  totalVotes = this?.count ?: 0,
  votes = this?.distribution?.mapNotNull { b ->
    b.stars?.let { Votes(value = it, count = b.count ?: 0) }
  },
)

private fun TrustResponse?.toTrust(): Trust? = this?.let {
  Trust(
    scanVerdict = it.scanVerdict,
    signerSha256 = it.signerSha256,
    signerConsistency = it.signerConsistency,
    provenance = it.provenance,
  )
}

private fun cm.aptoide.pt.feature_apps.data.deviceapi.model.ScreenshotResponse.toScreenshot(): Screenshot? =
  url?.let { Screenshot(url = it, height = height ?: 0, width = width ?: 0) }

private fun ArtifactResponse?.toFile(): File = File(
  md5 = this?.md5.orEmpty(),
  size = this?.sizeBytes ?: 0,
  path = this?.url.orEmpty(),
  path_alt = "",
)

private fun List<ArtifactResponse>.toAab(): Aab? {
  val splits = filter { it.kind == "split" }
  if (splits.isEmpty()) return null
  return Aab(
    requiredSplitTypes = emptyList(),
    baseSplits = splits.map { Split(type = it.filename ?: "split", file = it.toFile()) },
  )
}

private fun List<ArtifactResponse>.toObb(): Obb? {
  val main = firstOrNull { it.kind == "obb_main" } ?: return null
  val patch = firstOrNull { it.kind == "obb_patch" }
  return Obb(main = main.toFile(), patch = patch?.let { it.toFile() })
}
