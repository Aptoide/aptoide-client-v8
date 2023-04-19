package cm.aptoide.pt.feature_editorial.presentation

import cm.aptoide.pt.feature_editorial.domain.ArticleMeta

data class EditorialsCardUiState(
  val loading: Boolean,
  val editorialsMetas: List<ArticleMeta>,
)
