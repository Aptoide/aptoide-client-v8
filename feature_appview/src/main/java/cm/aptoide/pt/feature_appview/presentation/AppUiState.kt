package cm.aptoide.pt.feature_appview.presentation

import cm.aptoide.pt.feature_apps.data.App

data class AppUiState(
  val app: App?,
  val type: AppUiStateType,
  val selectedTab: Pair<AppViewTab, Int>,
  val tabsList: List<Pair<AppViewTab, Int>>,
  val similarAppsList: List<App>,
  val similarAppcAppsList: List<App>,
  val otherVersionsList: List<App>,
)

enum class AppUiStateType {
  IDLE, LOADING, NO_CONNECTION, ERROR
}
