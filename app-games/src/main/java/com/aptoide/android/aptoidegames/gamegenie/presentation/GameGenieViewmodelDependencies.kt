package com.aptoide.android.aptoidegames.gamegenie.presentation

import cm.aptoide.pt.feature_gamegenie.analytics.GameGenieAnalytics
import cm.aptoide.pt.feature_gamegenie.presentation.GameGenieUseCase
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface GameGenieViewModelDependencies {
  fun gameGenieUseCase(): GameGenieUseCase
  fun gameGenieAnalytics(): GameGenieAnalytics
}
