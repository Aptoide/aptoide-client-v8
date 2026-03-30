package com.aptoide.android.aptoidegames.gamesfeed.repository

interface GamesFeedLocalRepository {

  suspend fun saveGamesFeedVisibility(visibility: Boolean)
  suspend fun getGamesFeedVisibility(): Boolean?
  suspend fun saveLastGamesFeedNotificationTimestamp(timestamp: Long)
  suspend fun getLastGamesFeedNotificationTimestamp(): Long?
}
