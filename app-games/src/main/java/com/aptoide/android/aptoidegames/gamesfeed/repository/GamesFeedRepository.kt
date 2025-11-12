package com.aptoide.android.aptoidegames.gamesfeed.repository

/**
 * Repository for fetching games feed from Firebase Remote Config
 */
interface GamesFeedRepository {

  /**
   * Fetches the games feed from Firebase Remote Config
   * @return GamesFeedData containing list of videos and articles
   */
  suspend fun getGamesFeed(): GamesFeedData
}
