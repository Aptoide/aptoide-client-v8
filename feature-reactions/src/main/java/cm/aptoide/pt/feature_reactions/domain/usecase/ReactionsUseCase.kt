package cm.aptoide.pt.feature_reactions.domain.usecase

import cm.aptoide.pt.feature_reactions.ReactionsRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class ReactionsUseCase @Inject constructor(
  private val reactionsRepository: ReactionsRepository,
) {
  suspend fun getReactions(id: String) = reactionsRepository.getTotalReactions(id)

  suspend fun deleteReaction(id: String) = reactionsRepository.deleteReaction(id)

  suspend fun setReaction(id: String) = reactionsRepository.setReaction(id)
}
