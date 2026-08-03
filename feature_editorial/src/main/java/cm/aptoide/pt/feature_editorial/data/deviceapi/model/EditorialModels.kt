package cm.aptoide.pt.feature_editorial.data.deviceapi.model

import androidx.annotation.Keep

/**
 * DTOs for the editorial API (`api.dev.aptoide.com/editorials`), matching
 * `editorial.openapi.json`. Blocks are a discriminated union on `kind`; unknown
 * kinds deserialize to null (via DiscriminatorAdapterFactory) and are filtered.
 */

@Keep
data class EditorialListResponse(
  val items: List<EditorialListItemResponse>? = null,
  val nextCursor: String? = null,
)

@Keep
data class EditorialListItemResponse(
  val slug: String? = null,
  val locale: String? = null,
  val subtype: String? = null,
  val title: String? = null,
  val summary: String? = null,
  val coverImageUrl: String? = null,
  val publishedAt: String? = null,
  val viewCount: Int? = null,
)

@Keep
data class EditorialResponse(
  val slug: String? = null,
  val locale: String? = null,
  val subtype: String? = null,
  val title: String? = null,
  val summary: String? = null,
  val coverImageUrl: String? = null,
  val blocks: List<EditorialBlockResponse?>? = null,
  val publishedAt: String? = null,
  val viewCount: Int? = null,
)

sealed interface EditorialBlockResponse

@Keep
data class HeadingBlockResponse(val text: String? = null, val level: Int? = null) : EditorialBlockResponse

@Keep
data class ParagraphBlockResponse(val text: String? = null) : EditorialBlockResponse

@Keep
data class ImageBlockResponse(val url: String? = null, val alt: String? = null) : EditorialBlockResponse

@Keep
data class AppEmbedBlockResponse(
  val packageName: String? = null,
  val name: String? = null,
  val iconUrl: String? = null,
  val summary: String? = null,
) : EditorialBlockResponse

@Keep
data class VideoBlockResponse(val url: String? = null) : EditorialBlockResponse

@Keep
data class CtaActionBlockResponse(val label: String? = null, val url: String? = null) : EditorialBlockResponse
