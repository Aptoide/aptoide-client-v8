package cm.aptoide.pt.feature_gamegenie.data

import cm.aptoide.pt.feature_gamegenie.domain.GameCompanion
import kotlinx.coroutines.flow.Flow

interface GameCompanionsRepository {
  fun getCompanionGames(): Flow<List<GameCompanion>>
  suspend fun warmUpCache()
}
