package cm.aptoide.pt.feature_reactions

import cm.aptoide.pt.feature_reactions.data.Reaction
import cm.aptoide.pt.feature_reactions.data.Reactions

interface ReactionsRepository {
  suspend fun getTotalReactions(id: String?): Reactions
  suspend fun deleteReaction(id: String): Reaction
  suspend fun setReaction(id: String): Reaction
}
