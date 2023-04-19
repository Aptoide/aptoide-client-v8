package cm.aptoide.pt.feature_editorial.presentation

import cm.aptoide.pt.feature_editorial.domain.Article

data class EditorialUiState(
  val article: Article?,
  val type: EditorialUiStateType,
)

enum class EditorialUiStateType {
  IDLE, LOADING, NO_CONNECTION, ERROR
}
