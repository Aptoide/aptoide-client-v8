package cm.aptoide.pt.feature_oos.domain

import cm.aptoide.pt.feature_oos.repository.AvailableSpaceRepository
import cm.aptoide.pt.feature_oos.repository.UninstallObserver
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@ViewModelScoped
class AvailableSpaceUseCase @Inject constructor(
  private val availableSpaceRepository: AvailableSpaceRepository,
  private val uninstallObserver: UninstallObserver
) {

  fun getInitialRequiredSpace(appSize: Long): Long {
    return availableSpaceRepository.getRequiredSpace(appSize)
  }

  fun observeRequiredSpace(appSize: Long): Flow<Long> =
    uninstallObserver.onAppRemoved()
      .map {
        availableSpaceRepository.getRequiredSpace(appSize)
      }
}
