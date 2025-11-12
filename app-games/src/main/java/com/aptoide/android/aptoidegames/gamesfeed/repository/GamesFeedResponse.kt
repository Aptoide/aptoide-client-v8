package com.aptoide.android.aptoidegames.gamesfeed.repository

import androidx.annotation.Keep

/**
 * JSON deserialization model for Firebase Remote Config response
 */
@Keep
data class GamesFeedResponse(
  val items: List<GamesFeedItem>,
  val bundleGraphic: String? = null,
  val bundleIcon: String? = null
)
