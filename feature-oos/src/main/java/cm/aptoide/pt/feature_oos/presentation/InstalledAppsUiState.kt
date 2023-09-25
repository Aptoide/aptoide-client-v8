package cm.aptoide.pt.feature_oos.presentation

sealed class InstalledAppsUiState {
  data class Idle(val apps: List<String>) : InstalledAppsUiState()
  object Loading : InstalledAppsUiState()
}
