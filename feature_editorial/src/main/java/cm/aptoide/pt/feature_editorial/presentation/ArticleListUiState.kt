package cm.aptoide.pt.feature_editorial.presentation

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import cm.aptoide.pt.feature_editorial.domain.ArticleMeta
import cm.aptoide.pt.feature_editorial.domain.randomArticleMeta
import kotlin.random.Random
import kotlin.random.nextInt

sealed class ArticleListUiState {
  data class Idle(val articles: List<ArticleMeta>) : ArticleListUiState()
  object Loading : ArticleListUiState()
  object Empty : ArticleListUiState()
  object NoConnection : ArticleListUiState()
  object Error : ArticleListUiState()
}

class ArticleListUiStateProvider : PreviewParameterProvider<ArticleListUiState> {
  override val values: Sequence<ArticleListUiState> = sequenceOf(
    ArticleListUiState.Idle(List(Random.nextInt(1..12)) { randomArticleMeta }),
    ArticleListUiState.Loading,
    ArticleListUiState.Empty,
    ArticleListUiState.Error
  )
}

val previewArticlesListIdleState
  get() = ArticleListUiState.Idle(List(Random.nextInt(1..12)) { randomArticleMeta })
