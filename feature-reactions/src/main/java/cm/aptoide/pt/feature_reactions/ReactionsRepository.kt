package cm.aptoide.pt.feature_reactions

import kotlinx.coroutines.flow.Flow

interface ReactionsRepository {
  fun getTotalReactions(id: String?): Flow<ReactionsResult>

  sealed interface ReactionsResult {
    data class Success(val data: Reactions) : ReactionsResult
    data class Error(val e: Throwable) : ReactionsResult
  }
}
