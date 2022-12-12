package cm.aptoide.pt.feature_editorial.presentation

import cm.aptoide.pt.feature_editorial.domain.ArticleDetail

data class EditorialDetailUiState(val article: ArticleDetail?, val isLoading: Boolean)
