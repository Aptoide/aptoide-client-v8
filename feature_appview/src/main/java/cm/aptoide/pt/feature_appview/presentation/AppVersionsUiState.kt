package cm.aptoide.pt.feature_appview.presentation

import cm.aptoide.pt.feature_apps.data.App

sealed class AppVersionsUiState {
  object Loading : AppVersionsUiState()
  object Error : AppVersionsUiState()
  object NoConnection : AppVersionsUiState()
  data class Idle(val otherVersions: List<App>) : AppVersionsUiState()
}
