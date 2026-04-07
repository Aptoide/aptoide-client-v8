package com.aptoide.android.aptoidegames.promotions.data

import cm.aptoide.pt.feature_flags.domain.FeatureFlags
import com.aptoide.android.aptoidegames.promotions.data.model.PromotionJson
import com.aptoide.android.aptoidegames.promotions.domain.Promotion
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject

internal class AGPromotionsRepository @Inject constructor(
  private val featureFlags: FeatureFlags,
) : PromotionsRepository {

  override suspend fun getAllPromotions(): List<Promotion> {
    val jsonString = featureFlags.getFlagAsString(PROMOTIONS_KEY) ?: return emptyList()
    return runCatching {
      val type = object : TypeToken<List<PromotionJson>>() {}.type
      Gson().fromJson<List<PromotionJson>>(jsonString, type)
        .map { it.toDomainModel() }
    }.getOrDefault(emptyList())
  }

  companion object {
    private const val PROMOTIONS_KEY = "ahab_promotions_list"
  }
}

fun PromotionJson.toDomainModel() = Promotion(
  packageName = packageName,
  aliases = aliases.values.toSet(),
  image = image,
  title = title,
  content = content,
  uri = uri,
  userBonus = metadata?.userBonus ?: 0,
  uid = uid,
)
