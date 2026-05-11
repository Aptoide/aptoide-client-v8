package com.aptoide.android.aptoidegames.gamesfeed.repository

import cm.aptoide.pt.install_manager.InstallManager
import com.aptoide.android.aptoidegames.gamegenie.data.GamesFeedApiService
import javax.inject.Inject

/**
 * Retrofit implementation of GamesFeedRepository, fetching from the ForYou feed API.
 */
class AptoideGamesFeedRepository @Inject constructor(
  private val gameGenieApiService: GamesFeedApiService,
  private val installManager: InstallManager,
) : GamesFeedRepository {

  private var cachedTrackedGames: List<TrackedGame>? = null

  override suspend fun getGamesFeed(): GamesFeedData {
    val trackedPackageNames = getTrackedGames().map { it.packageName }.toSet()
    val installedPackages = installManager.installedApps
      .map { it.packageName }
      .filter { it in trackedPackageNames }
      .joinToString(",")
      .ifBlank { null }

    val response = gameGenieApiService.getForYouFeed(installedPackages)
    return GamesFeedData(
      items = response.items,
      bundleGraphic = response.bundleGraphic,
      bundleIcon = response.bundleIcon
    )
  }

  override suspend fun getTrackedGames(): List<TrackedGame> {
    return cachedTrackedGames ?: gameGenieApiService.getForYouGames().also {
      cachedTrackedGames = it
    }
  }
}
