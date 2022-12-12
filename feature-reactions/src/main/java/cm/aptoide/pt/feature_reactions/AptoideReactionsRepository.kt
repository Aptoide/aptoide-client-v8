package cm.aptoide.pt.feature_reactions

import cm.aptoide.pt.feature_reactions.data.Reaction
import cm.aptoide.pt.feature_reactions.data.Reactions
import cm.aptoide.pt.feature_reactions.data.TopReaction
import cm.aptoide.pt.feature_reactions.data.network.ReactionsJson
import cm.aptoide.pt.feature_reactions.data.network.TopReactionsJson

class AptoideReactionsRepository(private val reactionsRemoteService: ReactionsRemoteService) :
  ReactionsRepository {
  override suspend fun getTotalReactions(id: String?): ReactionsRepository.ReactionsResult =
    reactionsRemoteService.getReactions(id)
      .takeIf { it.isSuccessful }
      ?.body()
      ?.let { ReactionsRepository.ReactionsResult.Success(it.toDomainModel()) }
      ?: ReactionsRepository.ReactionsResult.Error(IllegalStateException())

  override suspend fun deleteReaction(id: String): ReactionsRepository.UpdateReactionResult =
    reactionsRemoteService.deleteReaction(id)
      .takeIf { it.isSuccessful }
      ?.body()
      ?.let { ReactionsRepository.UpdateReactionResult.Success(Reaction(id)) }
      ?: ReactionsRepository.UpdateReactionResult.Error(IllegalStateException())


  override suspend fun setReaction  (id: String): ReactionsRepository.UpdateReactionResult =
    reactionsRemoteService.setReaction(id)
      .takeIf { it.isSuccessful }
      ?.body()
      ?.let { ReactionsRepository.UpdateReactionResult.Success(Reaction(id)) }
      ?: ReactionsRepository.UpdateReactionResult.Error(IllegalStateException())
}

private fun ReactionsJson.toDomainModel(): Reactions =
  Reactions(total, top.map { it.toDomainModel() })

private fun TopReactionsJson.toDomainModel(): TopReaction = TopReaction(type, total)
