package com.aptoide.android.aptoidegames.editorial.data

import com.aptoide.android.aptoidegames.editorial.data.model.Block
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

/**
 * Resolves the editorial [Block] discriminated union on its `kind` field. Unrecognized
 * kinds return null (the contract is additive-only) so callers can [List.mapNotNull] them
 * away and never crash on a future block type.
 */
class BlockDeserializer : JsonDeserializer<Block?> {

  override fun deserialize(
    json: JsonElement,
    typeOfT: Type,
    context: JsonDeserializationContext,
  ): Block? {
    val obj = json.takeIf { it.isJsonObject }?.asJsonObject ?: return null
    val kind = obj.get("kind")?.takeIf { it.isJsonPrimitive }?.asString ?: return null
    return when (kind) {
      "heading" -> context.deserialize(obj, Block.Heading::class.java)
      "paragraph" -> context.deserialize(obj, Block.Paragraph::class.java)
      "image" -> context.deserialize(obj, Block.Image::class.java)
      "app_embed" -> context.deserialize(obj, Block.AppEmbed::class.java)
      "video" -> context.deserialize(obj, Block.Video::class.java)
      "cta_action" -> context.deserialize(obj, Block.CtaAction::class.java)
      else -> null
    }
  }
}
