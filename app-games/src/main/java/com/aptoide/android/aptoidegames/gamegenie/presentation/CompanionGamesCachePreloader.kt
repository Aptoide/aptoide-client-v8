package com.aptoide.android.aptoidegames.gamegenie.presentation

import cm.aptoide.pt.feature_flags.domain.FeatureFlags
import kotlinx.coroutines.CancellationException
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CompanionGamesCachePreloader @Inject constructor(
  private val getGameCompanionsUseCase: GetGameCompanionsUseCase,
  private val featureFlags: FeatureFlags,
) {

  suspend fun initialize() {
    if (!featureFlags.getFlag("show_game_genie", false)) return
    try {
      getGameCompanionsUseCase.warmUpCache()
    } catch (e: CancellationException) {
      throw e
    } catch (e: Throwable) {
      Timber.w(e, "Failed to preload companion games cache")
    }
  }
}
