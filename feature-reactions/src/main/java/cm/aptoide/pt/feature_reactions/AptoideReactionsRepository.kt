package cm.aptoide.pt.feature_reactions

import cm.aptoide.pt.feature_reactions.data.Reaction
import cm.aptoide.pt.feature_reactions.data.Reactions
import cm.aptoide.pt.feature_reactions.data.TopReaction
import cm.aptoide.pt.feature_reactions.data.network.ReactionsJson
import cm.aptoide.pt.feature_reactions.data.network.TopReactionsJson

class AptoideReactionsRepository(private val reactionsRemoteService: ReactionsRemoteService) :
  ReactionsRepository {
  override suspend fun getTotalReactions(id: String?): Reactions =
    reactionsRemoteService.getReactions(id).toDomainModel()

  override suspend fun deleteReaction(id: String): Reaction =
    reactionsRemoteService.deleteReaction(id).run { Reaction(id) }


  override suspend fun setReaction(id: String): Reaction =
    reactionsRemoteService.setReaction(id).run { Reaction(id) }
}

private fun ReactionsJson.toDomainModel(): Reactions =
  Reactions(total, top.map { it.toDomainModel() })

private fun TopReactionsJson.toDomainModel(): TopReaction = TopReaction(type, total)
