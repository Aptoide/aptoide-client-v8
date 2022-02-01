package cm.aptoide.pt.feature_search.domain.usecase

import cm.aptoide.pt.feature_search.domain.repository.SearchHistoryRepository

class SaveSearchHistoryUseCase(private val searchHistoryRepository: SearchHistoryRepository) {

  suspend fun addAppToSearchHistory(appName: String) {
    searchHistoryRepository.addAppToSearchHistory(appName)
  }
}