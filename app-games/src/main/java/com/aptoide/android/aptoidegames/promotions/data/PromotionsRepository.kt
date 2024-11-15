package com.aptoide.android.aptoidegames.promotions.data

import com.aptoide.android.aptoidegames.promotions.domain.Promotion

interface PromotionsRepository {

  suspend fun getAllPromotions(): List<Promotion>
}
