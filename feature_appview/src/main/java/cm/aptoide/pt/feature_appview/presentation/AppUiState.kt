package cm.aptoide.pt.feature_appview.presentation

import cm.aptoide.pt.feature_apps.data.App

sealed class AppUiState(var otherVersionsList: List<App> = emptyList()) {
  data class Idle(val app: App) : AppUiState()
  object Loading : AppUiState()
  object NoConnection : AppUiState()
  object Error : AppUiState()
}
