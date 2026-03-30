package com.aptoide.android.aptoidegames.gamesfeed.repository

/**
 * Repository for fetching games feed data.
 */
interface GamesFeedRepository {

  /**
   * Fetches the games feed from the ForYou feed API.
   * @return GamesFeedData containing list of videos and articles
   */
  suspend fun getGamesFeed(): GamesFeedData

  /**
   * Fetches the list of tracked games used to determine
   * whether to show the games feed feature.
   * @return list of tracked games with name and package name
   */
  suspend fun getTrackedGames(): List<TrackedGame>
}
