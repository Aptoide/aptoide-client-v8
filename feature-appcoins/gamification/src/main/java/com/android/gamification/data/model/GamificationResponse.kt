package com.android.gamification.data.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

@Keep
class GamificationResponse(
  id: String,
  priority: Int,
  gamificationStatus: GamificationStatus?,
  val bonus: Double,
  @SerializedName("current_amount_currency") val totalSpend: BigDecimal,
  @SerializedName("bonus_earned_currency") val totalEarned: BigDecimal,
  val level: Int,
  @SerializedName("next_level_amount_currency") val nextLevelAmount: BigDecimal?,
  val status: Status,
  val bundle: Boolean
) : PromotionsResponse(id, priority, gamificationStatus)
