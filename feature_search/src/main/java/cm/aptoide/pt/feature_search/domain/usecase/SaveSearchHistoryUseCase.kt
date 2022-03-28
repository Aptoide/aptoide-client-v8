package cm.aptoide.pt.feature_search.domain.usecase

import cm.aptoide.pt.feature_search.domain.repository.SearchRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class SaveSearchHistoryUseCase @Inject constructor(private val searchRepository: SearchRepository) {

  suspend fun addAppToSearchHistory(appName: String) {
    searchRepository.addAppToSearchHistory(appName)
  }
}