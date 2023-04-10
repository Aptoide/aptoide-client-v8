package cm.aptoide.pt.feature_categories.presentation

import cm.aptoide.pt.feature_categories.domain.Category

data class CategoriesViewUiState(
  val loading: Boolean,
  val categories: List<Category>
)
