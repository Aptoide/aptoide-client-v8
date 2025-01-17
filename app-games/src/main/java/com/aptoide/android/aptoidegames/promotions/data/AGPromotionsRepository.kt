package com.aptoide.android.aptoidegames.promotions.data

import com.aptoide.android.aptoidegames.promotions.data.model.PromotionJson
import com.aptoide.android.aptoidegames.promotions.domain.Promotion
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Inject

internal class AGPromotionsRepository @Inject constructor(
  private val promotionsApi: PromotionsApi,
  private val storeName: String,
) : PromotionsRepository {

  override suspend fun getAllPromotions(): List<Promotion> {
    return promotionsApi.getPromotionsList(storeName).map {
      it.toDomainModel()
    }
  }

  internal interface PromotionsApi {

    @GET("list-campaigns")
    suspend fun getPromotionsList(
      @Query("store_name") storeName: String,
      @Query("placement") placement: String = "HOME_DIALOG",
    ): List<PromotionJson>
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
