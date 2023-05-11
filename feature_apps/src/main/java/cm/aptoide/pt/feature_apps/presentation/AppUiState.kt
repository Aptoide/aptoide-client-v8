package cm.aptoide.pt.feature_apps.presentation

import cm.aptoide.pt.feature_apps.data.App

sealed class AppUiState {
  data class Idle(val app: App) : AppUiState()
  object Loading : AppUiState()
  object NoConnection : AppUiState()
  object Error : AppUiState()
}
