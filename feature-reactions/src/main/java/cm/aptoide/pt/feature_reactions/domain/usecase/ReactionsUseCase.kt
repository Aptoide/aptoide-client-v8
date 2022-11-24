package cm.aptoide.pt.feature_reactions.domain.usecase

import cm.aptoide.pt.feature_reactions.ReactionsRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class ReactionsUseCase @Inject constructor(
  private val reactionsRepository: ReactionsRepository
) {
  suspend fun getReactions(id: String) = reactionsRepository.getTotalReactions(id)
    .let { reactionsResult ->
      if (reactionsResult is ReactionsRepository.ReactionsResult.Success) {
        reactionsResult.data
      } else {
        throw IllegalStateException()
      }
    }
}
