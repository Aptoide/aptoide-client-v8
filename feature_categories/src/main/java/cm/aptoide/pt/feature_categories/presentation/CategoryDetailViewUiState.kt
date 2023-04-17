package cm.aptoide.pt.feature_categories.presentation

import cm.aptoide.pt.feature_apps.data.App

data class CategoryDetailViewUiState(
  val appList: List<App>,
  val categoryName: String,
  val type: CategoryDetailViewUiStateType,
)

enum class CategoryDetailViewUiStateType {
  IDLE, LOADING, EMPTY, NO_CONNECTION, ERROR
}
