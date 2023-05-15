package cm.aptoide.pt.feature_home.presentation

import cm.aptoide.pt.feature_apps.data.App

data class MoreBundleViewUiState(
  val appList: List<App>,
  val type: MoreBundleViewUiStateType,
)

enum class MoreBundleViewUiStateType {
  IDLE, LOADING, NO_CONNECTION, ERROR
}
