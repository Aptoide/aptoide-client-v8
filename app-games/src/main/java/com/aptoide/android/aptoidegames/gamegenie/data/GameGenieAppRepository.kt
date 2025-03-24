package com.aptoide.android.aptoidegames.gamegenie.data

import cm.aptoide.pt.feature_apps.data.App
import com.aptoide.android.aptoidegames.gamegenie.domain.GameContext
import kotlinx.coroutines.flow.Flow

interface GameGenieAppRepository {

  suspend fun getApp(packageName: String): App

  fun getInstalledApps(): Flow<List<GameContext>>
}
