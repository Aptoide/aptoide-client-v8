package cm.aptoide.pt.feature_categories.presentation

import cm.aptoide.pt.feature_apps.data.App

data class CategoryAppsViewUiState(
  val appList: List<App>,
  val categoryName: String,
  val type: CategoryAppsUiStateType,
)

enum class CategoryAppsUiStateType {
  IDLE, LOADING, EMPTY, NO_CONNECTION, ERROR
}
