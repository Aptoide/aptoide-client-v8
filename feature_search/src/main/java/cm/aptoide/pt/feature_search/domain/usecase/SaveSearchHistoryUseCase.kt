package cm.aptoide.pt.feature_search.domain.usecase

import cm.aptoide.pt.feature_search.domain.repository.SearchRepository

class SaveSearchHistoryUseCase(private val searchRepository: SearchRepository) {

  suspend fun addAppToSearchHistory(appName: String) {
    searchRepository.addAppToSearchHistory(appName)
  }
}