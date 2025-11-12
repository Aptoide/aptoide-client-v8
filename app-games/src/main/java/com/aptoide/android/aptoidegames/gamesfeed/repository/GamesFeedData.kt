package com.aptoide.android.aptoidegames.gamesfeed.repository

import androidx.annotation.Keep

/**
 * Domain model for games feed data
 */
@Keep
data class GamesFeedData(
  val items: List<GamesFeedItem>,
  val bundleGraphic: String? = null,
  val bundleIcon: String? = null
)
