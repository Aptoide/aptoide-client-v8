package com.aptoide.android.aptoidegames.editorial.data.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

/**
 * DTOs for the new public Editorial read API (api.dev.aptoide.com / api.aptoide.com).
 * Modeled exactly per the frozen OpenAPI contract (additive-only). Nullable fields mirror
 * the contract's optional (`?`) fields. Parsed with Gson ([BlockDeserializer] for [Block]).
 */
@Keep
data class EditorialListResponse(
  @SerializedName("items") val items: List<EditorialListItem> = emptyList(),
  @SerializedName("next_cursor") val nextCursor: String? = null,
)

@Keep
data class EditorialListItem(
  @SerializedName("slug") val slug: String,
  @SerializedName("locale") val locale: String? = null,
  @SerializedName("subtype") val subtype: String? = null,
  @SerializedName("title") val title: String = "",
  @SerializedName("summary") val summary: String? = null,
  @SerializedName("cover_image_url") val coverImageUrl: String? = null,
  @SerializedName("published_at") val publishedAt: String? = null,
  @SerializedName("view_count") val viewCount: Long = 0,
)

@Keep
data class EditorialResponse(
  @SerializedName("slug") val slug: String,
  @SerializedName("locale") val locale: String? = null,
  @SerializedName("subtype") val subtype: String? = null,
  @SerializedName("title") val title: String = "",
  @SerializedName("summary") val summary: String? = null,
  @SerializedName("cover_image_url") val coverImageUrl: String? = null,
  // Nullable elements: unrecognized future `kind`s deserialize to null and are filtered out.
  @SerializedName("blocks") val blocks: List<Block?> = emptyList(),
  @SerializedName("published_at") val publishedAt: String? = null,
  @SerializedName("view_count") val viewCount: Long = 0,
)

/**
 * Discriminated union on the `kind` field. See [BlockDeserializer]. The contract is
 * additive-only, so unknown kinds must be skipped gracefully (parsed as null).
 */
@Keep
sealed interface Block {

  @Keep
  data class Heading(
    @SerializedName("text") val text: String = "",
    @SerializedName("level") val level: Int = 1,
  ) : Block

  @Keep
  data class Paragraph(
    @SerializedName("text") val text: String = "",
  ) : Block

  @Keep
  data class Image(
    @SerializedName("url") val url: String = "",
    @SerializedName("alt") val alt: String? = null,
  ) : Block

  @Keep
  data class AppEmbed(
    @SerializedName("package_name") val packageName: String = "",
    @SerializedName("name") val name: String = "",
    @SerializedName("icon_url") val iconUrl: String? = null,
    @SerializedName("summary") val summary: String? = null,
  ) : Block

  @Keep
  data class Video(
    @SerializedName("url") val url: String = "",
  ) : Block

  @Keep
  data class CtaAction(
    @SerializedName("label") val label: String = "",
    @SerializedName("url") val url: String = "",
  ) : Block
}
