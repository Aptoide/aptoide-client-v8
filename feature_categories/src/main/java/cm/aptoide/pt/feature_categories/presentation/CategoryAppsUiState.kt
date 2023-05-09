package cm.aptoide.pt.feature_categories.presentation

import cm.aptoide.pt.feature_apps.data.App

sealed class CategoryAppsUiState {
  data class Idle(val appList: List<App>) : CategoryAppsUiState()
  object Loading : CategoryAppsUiState()
  object Empty : CategoryAppsUiState()
  object NoConnection : CategoryAppsUiState()
  object Error : CategoryAppsUiState()
}
