package cm.aptoide.pt.feature_editorial.data

import kotlinx.coroutines.flow.Flow

interface EditorialRepository {
  fun getLatestArticle(): Flow<EditorialResult>
  fun getArticleDetail(articleId: String): Flow<EditorialDetailResult>
  fun getArticleMeta(editorialWidgetUrl: String): Flow<EditorialResult>

  sealed interface EditorialResult {
    data class Success(val data: Article) : EditorialResult
    data class Error(val e: Throwable) : EditorialResult

  }

  sealed interface EditorialDetailResult {
    data class Success(val data: ArticleDetail) : EditorialDetailResult
    data class Error(val e: Throwable) : EditorialDetailResult

  }
}
