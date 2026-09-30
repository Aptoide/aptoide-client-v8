package cm.aptoide.pt.feature_apps.data.deviceapi.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

// The app payloads of the device API, as in its device.openapi.json contract. Every field is
// nullable, whatever the contract requires, so that a partial payload costs a field and not
// the whole response.

@Keep
data class AppSummaryResponse(
  @SerializedName("package_name") val packageName: String? = null,
  @SerializedName("name") val name: String? = null,
  @SerializedName("icon_url") val iconUrl: String? = null,
  // Totals across sources, which go well beyond what an Int holds
  @SerializedName("downloads") val downloads: Long? = null,
  @SerializedName("rating") val rating: RatingResponse? = null,
  @SerializedName("feature_graphic_url") val featureGraphicUrl: String? = null,
  @SerializedName("updated_at") val updatedAt: String? = null,
  @SerializedName("aptoide_billing") val aptoideBilling: Boolean? = null,
)

@Keep
data class AppResponse(
  @SerializedName("package_name") val packageName: String? = null,
  @SerializedName("name") val name: String? = null,
  @SerializedName("icon_url") val iconUrl: String? = null,
  @SerializedName("publisher_name") val publisherName: String? = null,
  @SerializedName("summary") val summary: String? = null,
  @SerializedName("description") val description: String? = null,
  @SerializedName("downloads") val downloads: Long? = null,
  @SerializedName("aptoide_downloads") val aptoideDownloads: Long? = null,
  @SerializedName("rating") val rating: RatingResponse? = null,
  @SerializedName("screenshots") val screenshots: List<ScreenshotResponse>? = null,
  @SerializedName("videos") val videos: List<VideoResponse>? = null,
  @SerializedName("feature_graphic_url") val featureGraphicUrl: String? = null,
  @SerializedName("age_rating") val ageRating: AgeRatingResponse? = null,
  @SerializedName("whats_new") val whatsNew: String? = null,
  @SerializedName("developer_website") val developerWebsite: String? = null,
  @SerializedName("developer_email") val developerEmail: String? = null,
  @SerializedName("privacy_policy_url") val privacyPolicyUrl: String? = null,
  @SerializedName("aptoide_billing") val aptoideBilling: Boolean? = null,
  @SerializedName("release") val release: ReleaseResponse? = null,
)

@Keep
data class ScreenshotResponse(
  @SerializedName("url") val url: String? = null,
  @SerializedName("width") val width: Int? = null,
  @SerializedName("height") val height: Int? = null,
)

@Keep
data class VideoResponse(
  @SerializedName("url") val url: String? = null,
  @SerializedName("thumbnail_url") val thumbnailUrl: String? = null,
  @SerializedName("kind") val kind: String? = null,
)

@Keep
data class AgeRatingResponse(
  @SerializedName("rating") val rating: Int? = null,
  @SerializedName("label") val label: String? = null,
  @SerializedName("code") val code: String? = null,
)

@Keep
data class ReleaseResponse(
  @SerializedName("version_name") val versionName: String? = null,
  @SerializedName("version_code") val versionCode: Int? = null,
  @SerializedName("min_sdk") val minSdk: Int? = null,
  @SerializedName("size_bytes") val sizeBytes: Long? = null,
  @SerializedName("released_at") val releasedAt: String? = null,
  @SerializedName("artifacts") val artifacts: List<ArtifactResponse>? = null,
  @SerializedName("permissions") val permissions: List<String>? = null,
)

@Keep
data class ArtifactResponse(
  /** `apk`, `split`, `obb_main` or `obb_patch`. */
  @SerializedName("kind") val kind: String? = null,
  @SerializedName("url") val url: String? = null,
  @SerializedName("size_bytes") val sizeBytes: Long? = null,
  @SerializedName("md5") val md5: String? = null,
  @SerializedName("filename") val filename: String? = null,
)

@Keep
data class RatingResponse(
  @SerializedName("average") val average: Double? = null,
  @SerializedName("count") val count: Long? = null,
  @SerializedName("distribution") val distribution: List<RatingBucketResponse>? = null,
)

@Keep
data class RatingBucketResponse(
  @SerializedName("stars") val stars: Int? = null,
  @SerializedName("count") val count: Int? = null,
)

/** A page of `GET apps`, which serves searches and listings alike. */
@Keep
data class AppsPageResponse(
  @SerializedName("items") val items: List<AppSummaryResponse>? = null,
  @SerializedName("next_cursor") val nextCursor: String? = null,
)

/** `GET apps/{package}/related`, which is not paginated. */
@Keep
data class RelatedAppsResponse(
  @SerializedName("items") val items: List<AppSummaryResponse>? = null,
)
