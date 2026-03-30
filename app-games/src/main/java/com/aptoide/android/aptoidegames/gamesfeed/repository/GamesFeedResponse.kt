package com.aptoide.android.aptoidegames.gamesfeed.repository

import androidx.annotation.Keep

/**
 * JSON deserialization model for the ForYou feed API response.
 */
@Keep
data class GamesFeedResponse(
  val items: List<GamesFeedItem>,
  val bundleGraphic: String? = null,
  val bundleIcon: String? = null
)
