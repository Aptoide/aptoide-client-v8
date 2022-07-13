package cm.aptoide.pt.feature_editorial.data

import kotlinx.coroutines.flow.Flow

interface EditorialRepository {
  fun getLatestArticle(): Flow<EditorialResult>

  sealed interface EditorialResult {
    data class Success(val data: Article) : EditorialResult
    data class Error(val e: Throwable) : EditorialResult

  }
}
