package cm.aptoide.pt.feature_reactions

import cm.aptoide.pt.feature_reactions.data.network.ReactionsJson

class AptoideReactionsRepository(private val reactionsRemoteService: ReactionsRemoteService) :
  ReactionsRepository {
  override suspend fun getTotalReactions(id: String?): ReactionsRepository.ReactionsResult =
    reactionsRemoteService.getReactions(id)
      .takeIf { it.isSuccessful }
      ?.body()
      ?.let { ReactionsRepository.ReactionsResult.Success(it.toDomainModel()) }
      ?: ReactionsRepository.ReactionsResult.Error(IllegalStateException())
}

private fun ReactionsJson.toDomainModel(): Reactions {
  return Reactions(this.total)
}