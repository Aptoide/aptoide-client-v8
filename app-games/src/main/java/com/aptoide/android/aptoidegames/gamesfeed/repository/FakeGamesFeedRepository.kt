package com.aptoide.android.aptoidegames.gamesfeed.repository

import com.google.gson.Gson
import javax.inject.Inject

/**
 * Fake implementation of GamesFeedRepository with hardcoded mock data
 * for development and testing purposes
 */
class FakeGamesFeedRepository @Inject constructor() : GamesFeedRepository {

  private val gson = Gson()

  override suspend fun getGamesFeed(): GamesFeedData {
    val response = gson.fromJson(MockGamesFeedData.MOCK_JSON, GamesFeedResponse::class.java)
    return GamesFeedData(
      items = response.items,
      bundleGraphic = response.bundleGraphic,
      bundleIcon = response.bundleIcon
    )
  }
}
