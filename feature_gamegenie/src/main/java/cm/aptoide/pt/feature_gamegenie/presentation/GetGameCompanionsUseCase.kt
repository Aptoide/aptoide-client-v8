package cm.aptoide.pt.feature_gamegenie.presentation

import cm.aptoide.pt.feature_gamegenie.data.GameCompanionsRepository
import cm.aptoide.pt.feature_gamegenie.domain.GameCompanion
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
