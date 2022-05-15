package cm.aptoide.pt.feature_appview.presentation

import cm.aptoide.pt.feature_apps.data.App

data class AppViewUiState(
  val app: App?,
  val isLoading: Boolean,
  val selectedTab: AppViewTab,
  val tabsList: List<AppViewTab>
)