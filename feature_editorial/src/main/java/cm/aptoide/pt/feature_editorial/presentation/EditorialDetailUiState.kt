package cm.aptoide.pt.feature_editorial.presentation

import cm.aptoide.pt.feature_editorial.domain.ArticleDetail

data class EditorialDetailUiState(
  val article: ArticleDetail?,
  val type: EditorialDetailUiStateType,
)

enum class EditorialDetailUiStateType {
  IDLE, LOADING, NO_CONNECTION, ERROR
}
