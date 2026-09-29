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
