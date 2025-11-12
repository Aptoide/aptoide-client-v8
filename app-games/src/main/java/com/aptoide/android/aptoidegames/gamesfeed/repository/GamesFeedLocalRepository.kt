package com.aptoide.android.aptoidegames.gamesfeed.repository

interface GamesFeedLocalRepository {

  suspend fun saveGamesFeedVisibility(visibility: Boolean)
  suspend fun getGamesFeedVisibility(): Boolean?
}
