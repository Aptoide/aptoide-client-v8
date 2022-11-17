package cm.aptoide.pt.feature_reactions

interface ReactionsRepository {
  suspend fun getTotalReactions(id: String?): ReactionsResult

  sealed interface ReactionsResult {
    data class Success(val data: Reactions) : ReactionsResult
    data class Error(val e: Throwable) : ReactionsResult
  }
}
