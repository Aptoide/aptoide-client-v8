package cm.aptoide.pt.feature_editorial.presentation

import cm.aptoide.pt.feature_editorial.domain.Article

sealed class EditorialUiState {
  object Loading : EditorialUiState()
  object NoConnection : EditorialUiState()
  object Error : EditorialUiState()
  data class Idle(val article: Article) : EditorialUiState()
}
