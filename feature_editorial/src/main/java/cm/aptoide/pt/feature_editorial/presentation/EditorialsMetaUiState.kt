package cm.aptoide.pt.feature_editorial.presentation

import cm.aptoide.pt.feature_editorial.domain.EditorialMeta

data class EditorialsMetaUiState(
  val loading: Boolean,
  val editorialsMetas: List<EditorialMeta>,
)
