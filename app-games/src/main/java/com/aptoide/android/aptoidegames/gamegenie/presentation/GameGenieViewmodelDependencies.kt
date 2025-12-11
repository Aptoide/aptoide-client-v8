package com.aptoide.android.aptoidegames.gamegenie.presentation

import com.aptoide.android.aptoidegames.gamegenie.analytics.GameGenieAnalytics
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface GameGenieViewModelDependencies {
  fun gameGenieUseCase(): GameGenieUseCase
  fun gameGenieAnalytics(): GameGenieAnalytics
}
