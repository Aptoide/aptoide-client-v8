package cm.aptoide.pt.feature_apps.data.deviceapi.model

import androidx.annotation.Keep

/**
 * DTOs for the new device API (`api.dev.aptoide.com/android/v1`), matching
 * `device.openapi.json`. Fields are camelCase; the device Retrofit's Gson uses
 * LOWER_CASE_WITH_UNDERSCORES so `packageName` binds `package_name`, etc.
 * Shared across surfaces that return apps (home/search/listings/related/detail).
 */

@Keep
data class AppSummaryResponse(
  val packageName: String? = null,
  val name: String? = null,
  val iconUrl: String? = null,
  val featureGraphicUrl: String? = null,
  val downloads: Int? = null,
  val rating: RatingResponse? = null,
)

@Keep
data class AppResponse(
  val packageName: String? = null,
  val name: String? = null,
  val iconUrl: String? = null,
  val publisherName: String? = null,
  val summary: String? = null,
  val description: String? = null,
  val downloads: Int? = null,
  val aptoideDownloads: Int? = null,
  val rating: RatingResponse? = null,
  val screenshots: List<ScreenshotResponse>? = null,
  val videos: List<VideoResponse>? = null,
  val featureGraphicUrl: String? = null,
  val ageRating: AgeRatingResponse? = null,
  val release: ReleaseResponse? = null,
  val trust: TrustResponse? = null,
)

@Keep
data class RatingResponse(
  val average: Double? = null,
  val count: Long? = null,
  val distribution: List<RatingBucketResponse>? = null,
)

@Keep
data class RatingBucketResponse(
  val stars: Int? = null,
  val count: Int? = null,
)

@Keep
data class ScreenshotResponse(
  val url: String? = null,
  val width: Int? = null,
  val height: Int? = null,
)

@Keep
data class VideoResponse(
  val url: String? = null,
  val thumbnailUrl: String? = null,
  val kind: String? = null,
)

@Keep
data class AgeRatingResponse(
  val rating: Int? = null,
  val label: String? = null,
  val code: String? = null,
)

@Keep
data class ReleaseResponse(
  val versionName: String? = null,
  val versionCode: Int? = null,
  val minSdk: Int? = null,
  val sizeBytes: Long? = null,
  val releasedAt: String? = null,
  val artifacts: List<ArtifactResponse>? = null,
)

@Keep
data class ArtifactResponse(
  /** apk | split | obb_main | obb_patch */
  val kind: String? = null,
  val url: String? = null,
  val sizeBytes: Long? = null,
  val md5: String? = null,
  val filename: String? = null,
)

@Keep
data class TrustResponse(
  val scanVerdict: String? = null,
  val signerSha256: String? = null,
  val signerConsistency: String? = null,
  val provenance: String? = null,
)

/** `GET /apps` (search/listing) and `GET /apps/{package}/reviews` share this cursored envelope. */
@Keep
data class AppSummaryPageResponse(
  val items: List<AppSummaryResponse>? = null,
  val nextCursor: String? = null,
)

/** `GET /apps/{package}/related` — unpaginated carousel. */
@Keep
data class RelatedAppsResponse(
  val items: List<AppSummaryResponse>? = null,
)

/** `GET /apps/{package}/releases` — newest-first, cursored version history. */
@Keep
data class ReleaseHistoryResponse(
  val items: List<ReleaseHistoryItemResponse>? = null,
  val nextCursor: String? = null,
)

@Keep
data class ReleaseHistoryItemResponse(
  val release: ReleaseResponse? = null,
  val trust: TrustResponse? = null,
)
