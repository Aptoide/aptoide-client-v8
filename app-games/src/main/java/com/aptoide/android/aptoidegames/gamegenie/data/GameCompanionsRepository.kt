package com.aptoide.android.aptoidegames.gamegenie.data

import com.aptoide.android.aptoidegames.gamegenie.domain.GameCompanion
import kotlinx.coroutines.flow.Flow

interface GameCompanionsRepository {
  fun getCompanionGames(): Flow<List<GameCompanion>>
  suspend fun warmUpCache()
}
