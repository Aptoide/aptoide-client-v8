package cm.aptoide.pt.feature_apps.presentation

import cm.aptoide.pt.feature_apps.data.App

sealed class AppsListUiState {
  data class Idle(val apps: List<App>) : AppsListUiState()
  object Loading : AppsListUiState()
  object Empty : AppsListUiState()
  object NoConnection : AppsListUiState()
  object Error : AppsListUiState()
}
