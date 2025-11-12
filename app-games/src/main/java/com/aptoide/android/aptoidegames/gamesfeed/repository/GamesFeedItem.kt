package com.aptoide.android.aptoidegames.gamesfeed.repository

import androidx.annotation.Keep

/**
 * Represents a single item in the games feed (video or article)
 */
@Keep
data class GamesFeedItem(
  val id: String,
  val type: GamesFeedItemType,
  val title: String,
  val description: String? = null,
  val featureGraphic: String? = null,
  val authorName: String? = null,
  val authorLogo: String? = null,
  val publishedAt: String? = null,
  val url: String? = null
)
