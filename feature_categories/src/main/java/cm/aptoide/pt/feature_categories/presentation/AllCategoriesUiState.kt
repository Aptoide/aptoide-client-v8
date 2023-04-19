package cm.aptoide.pt.feature_categories.presentation

import cm.aptoide.pt.feature_categories.domain.Category

data class AllCategoriesUiState(
  val categoryList: List<Category>,
  val categoryBundleTag: String,
  val type: AllCategoriesUiStateType,
)

enum class AllCategoriesUiStateType {
  IDLE, LOADING, NO_CONNECTION, ERROR
}