package com.aptoide.android.aptoidegames.gamegenie.data

import com.aptoide.android.aptoidegames.gamesfeed.repository.GamesFeedResponse
import com.aptoide.android.aptoidegames.gamesfeed.repository.TrackedGame
import retrofit2.http.GET
import retrofit2.http.Query

interface GamesFeedApiService {
  @GET("api/foryou/feed")
  suspend fun getForYouFeed(
    @Query("installed_packages") installedPackages: String? = null,
  ): GamesFeedResponse

  @GET("api/foryou/games")
  suspend fun getForYouGames(): List<TrackedGame>
}
