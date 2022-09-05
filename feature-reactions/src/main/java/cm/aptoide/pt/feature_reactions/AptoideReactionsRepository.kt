package cm.aptoide.pt.feature_reactions

import cm.aptoide.pt.feature_reactions.data.network.ReactionsJson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AptoideReactionsRepository(private val reactionsRemoteService: ReactionsRemoteService) :
  ReactionsRepository {
  override fun getTotalReactions(id: String?): Flow<ReactionsRepository.ReactionsResult> = flow {
    val reactionsResponse = reactionsRemoteService.getReactions(id)

    if (reactionsResponse.isSuccessful) {
      reactionsResponse.body()?.let {
        emit(ReactionsRepository.ReactionsResult.Success(it.toDomainModel()))
      }
    } else {
      emit(ReactionsRepository.ReactionsResult.Error(IllegalStateException()))
    }

  }
}

private fun ReactionsJson.toDomainModel(): Reactions {
  return Reactions(this.total)
}