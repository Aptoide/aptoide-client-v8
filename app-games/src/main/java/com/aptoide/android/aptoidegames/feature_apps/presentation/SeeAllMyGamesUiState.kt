package com.aptoide.android.aptoidegames.feature_apps.presentation

import cm.aptoide.pt.feature_apps.data.MyGamesApp

sealed class SeeAllMyGamesUiState {
  object Loading : SeeAllMyGamesUiState()
  data class AppsList(val installedAppsList: List<MyGamesApp>) : SeeAllMyGamesUiState()
}
