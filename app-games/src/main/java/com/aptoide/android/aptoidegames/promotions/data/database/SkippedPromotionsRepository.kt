package com.aptoide.android.aptoidegames.promotions.data.database

interface SkippedPromotionsRepository {

  suspend fun getSkippedPromotions(): List<String>

  suspend fun skipPromotion(packageName: String)
}
