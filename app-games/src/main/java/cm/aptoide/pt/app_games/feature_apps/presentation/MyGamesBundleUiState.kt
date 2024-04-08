package cm.aptoide.pt.app_games.feature_apps.presentation

import cm.aptoide.pt.feature_apps.data.MyGamesApp

sealed class MyGamesBundleUiState {
  object Loading : MyGamesBundleUiState()
  object Empty : MyGamesBundleUiState()
  data class AppsList(val installedAppsList: List<MyGamesApp>) : MyGamesBundleUiState()
}