package com.aptoide.android.aptoidegames.play_and_earn.domain

data class UserStats(
  val currentAmount: Float,
  val currentAmountCurrency: Float,
  val level: Int,
  val nextLevelAmount: Long?,
  val nextLevelAmountCurrency: Long?,
)
