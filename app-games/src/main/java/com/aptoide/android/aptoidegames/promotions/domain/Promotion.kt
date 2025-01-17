package com.aptoide.android.aptoidegames.promotions.domain

data class Promotion(
  val packageName: String,
  val aliases: Set<String>,
  val image: String,
  val title: String,
  val content: String,
  val uri: String,
  val userBonus: Int,
  val uid: String,
)
