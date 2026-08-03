package cm.aptoide.pt.feature_home.data.deviceapi.model

import androidx.annotation.Keep
import cm.aptoide.pt.feature_apps.data.deviceapi.model.AppSummaryResponse

/**
 * `GET /android/v1/home` — typed, client-rendered sections (`device.openapi.json`,
 * D-007). Sections are a discriminated union on `type`; unknown kinds deserialize
 * to null (via DiscriminatorAdapterFactory) and are filtered. camelCase fields bind
 * snake_case via the device Retrofit's Gson naming policy.
 */
@Keep
data class HomeResponse(
  val variant: String? = null,
  val sections: List<HomeSectionResponse?>? = null,
)

sealed interface HomeSectionResponse

@Keep
data class FeaturedBannerSectionResponse(
  val app: AppSummaryResponse? = null,
  val tagline: String? = null,
  val imageUrl: String? = null,
) : HomeSectionResponse

@Keep
data class AppCollectionSectionResponse(
  val title: String? = null,
  val apps: List<AppSummaryResponse>? = null,
) : HomeSectionResponse

@Keep
data class TopChartSectionResponse(
  val title: String? = null,
  val apps: List<AppSummaryResponse>? = null,
) : HomeSectionResponse

@Keep
data class CategoryGridSectionResponse(
  val title: String? = null,
  val categories: List<HomeCategoryResponse>? = null,
) : HomeSectionResponse

@Keep
data class EditorialCardSectionResponse(
  val slug: String? = null,
  val title: String? = null,
  val coverImageUrl: String? = null,
) : HomeSectionResponse

@Keep
data class HomeCategoryResponse(
  val slug: String? = null,
  val title: String? = null,
  val parent: String? = null,
  val appCount: Int? = null,
)
