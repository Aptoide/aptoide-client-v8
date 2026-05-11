package cm.aptoide.pt.feature_gamegenie.data

import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_gamegenie.domain.GameContext
import kotlinx.coroutines.flow.Flow

interface GameGenieAppRepository {

  suspend fun getApp(packageName: String): App

  fun getInstalledApps(): Flow<List<GameContext>>
}
