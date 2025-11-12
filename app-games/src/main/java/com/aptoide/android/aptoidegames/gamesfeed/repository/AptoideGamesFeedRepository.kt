package com.aptoide.android.aptoidegames.gamesfeed.repository

import cm.aptoide.pt.feature_flags.domain.FeatureFlags
import com.google.gson.Gson
import javax.inject.Inject

/**
 * Firebase Remote Config implementation of GamesFeedRepository
 */
class AptoideGamesFeedRepository @Inject constructor(private val featureFlags: FeatureFlags) :
  GamesFeedRepository {

  companion object {
    private const val GAMES_FEED_KEY = "gamesfeed_content"
  }

  override suspend fun getGamesFeed(): GamesFeedData {
    val gson = Gson()
    val gamesFeed = featureFlags.getFlagAsString(GAMES_FEED_KEY)
    return if (gamesFeed.isNullOrBlank()) {
      GamesFeedData(items = emptyList(), bundleGraphic = null, bundleIcon = null)
    } else {
      val gamesFeedResponse = gson.fromJson(gamesFeed, GamesFeedResponse::class.java)
      GamesFeedData(
        items = gamesFeedResponse.items,
        bundleGraphic = gamesFeedResponse.bundleGraphic,
        bundleIcon = gamesFeedResponse.bundleIcon
      )
    }
  }
}
