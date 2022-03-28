package cm.aptoide.pt.feature_search.domain.usecase

import cm.aptoide.pt.feature_search.domain.repository.SearchRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class RemoveSearchHistoryUseCase @Inject constructor(private val searchRepository: SearchRepository) {

  suspend fun removeSearchHistoryApp(appName: String) {
    searchRepository.removeAppFromSearchHistory(appName)
  }
}