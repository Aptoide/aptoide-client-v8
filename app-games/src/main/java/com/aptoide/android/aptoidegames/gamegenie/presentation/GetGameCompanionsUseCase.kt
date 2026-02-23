package com.aptoide.android.aptoidegames.gamegenie.presentation

import com.aptoide.android.aptoidegames.gamegenie.data.GameCompanionsRepository
import com.aptoide.android.aptoidegames.gamegenie.domain.GameCompanion
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetGameCompanionsUseCase @Inject constructor(
  private val repository: GameCompanionsRepository,
) {

  fun getCompanionGames(): Flow<List<GameCompanion>> = repository.getCompanionGames()

  suspend fun warmUpCache() = repository.warmUpCache()
}
