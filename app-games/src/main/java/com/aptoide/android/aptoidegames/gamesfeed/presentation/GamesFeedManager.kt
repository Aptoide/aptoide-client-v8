package com.aptoide.android.aptoidegames.gamesfeed.presentation

import android.content.Context
import cm.aptoide.pt.extensions.isAppInstalled
import cm.aptoide.pt.feature_flags.domain.FeatureFlags
import cm.aptoide.pt.install_manager.InstallManager
import com.aptoide.android.aptoidegames.gamesfeed.analytics.GamesFeedAnalytics
import com.aptoide.android.aptoidegames.gamesfeed.repository.GamesFeedLocalRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GamesFeedManager @Inject constructor(
  private val installManager: InstallManager,
  private val featureFlags: FeatureFlags,
  private val gamesFeedLocalRepository: GamesFeedLocalRepository,
  private val gamesFeedAnalytics: GamesFeedAnalytics,
  @ApplicationContext private val context: Context
) {

  companion object {
    private const val ROBLOX_PACKAGE = "com.roblox.client"
    private const val GAMES_FEED_AB_TEST_FLAG = "games_feed"
  }

  /**
   * Returns a Flow that emits the games feed visibility based on A/B test and Roblox installation.
   *
   * Logic:
   * - A/B test only applies to users with Roblox installed
   * - If Roblox is installed: emits the A/B test value (true/false) + sends activation event
   * - If Roblox is NOT installed: emits false first, waits for installation, then emits A/B test value + sends activation event
   *
   * The activation event is always sent once Roblox is installed (regardless of A/B test result).
   */
  fun shouldShowGamesFeed(): Flow<Boolean> = flow {
    if (gamesFeedLocalRepository.getGamesFeedVisibility() != null) {
      emit(gamesFeedLocalRepository.getGamesFeedVisibility()!!)
    } else {
      val shouldShowGamesFeedAbTest = featureFlags.getFlag(GAMES_FEED_AB_TEST_FLAG, false)
      if (context.isAppInstalled(ROBLOX_PACKAGE)) {
        Timber.d("Roblox is already installed. A/B test result: $shouldShowGamesFeedAbTest")
        gamesFeedAnalytics.sendGamesFeedRobloxInstalled()
        gamesFeedLocalRepository.saveGamesFeedVisibility(visibility = shouldShowGamesFeedAbTest)
        emit(shouldShowGamesFeedAbTest)
      } else {
        Timber.d("Roblox is not installed. A/B test result: $shouldShowGamesFeedAbTest. Emitting false and observing for installation...")

        emit(false)

        installManager.appsChanges
          .filter { app -> app.packageName == ROBLOX_PACKAGE }
          .filter { app -> app.packageInfo != null }
          .first()

        if (context.isAppInstalled(ROBLOX_PACKAGE)) {
          Timber.d("Roblox has been installed! A/B test result: $shouldShowGamesFeedAbTest")
          gamesFeedAnalytics.sendGamesFeedRobloxInstalled()
          gamesFeedLocalRepository.saveGamesFeedVisibility(visibility = shouldShowGamesFeedAbTest)
          emit(shouldShowGamesFeedAbTest)
        }
      }
    }
  }
}
