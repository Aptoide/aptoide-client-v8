package cm.aptoide.pt.feature_reactions

import cm.aptoide.pt.feature_reactions.data.Reaction
import cm.aptoide.pt.feature_reactions.data.Reactions

interface ReactionsRepository {
  suspend fun getTotalReactions(id: String?): ReactionsResult
  suspend fun deleteReaction(id: String): UpdateReactionResult
  suspend fun setReaction(id: String): UpdateReactionResult

  sealed interface ReactionsResult {
    data class Success(val data: Reactions) : ReactionsResult
    data class Error(val e: Throwable) : ReactionsResult
  }

  sealed interface UpdateReactionResult {
    data class Success(val data: Reaction) : UpdateReactionResult
    data class Error(val e: Throwable) : UpdateReactionResult
  }
}
