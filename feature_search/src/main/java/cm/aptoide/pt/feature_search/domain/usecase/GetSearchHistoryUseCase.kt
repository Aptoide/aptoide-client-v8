package cm.aptoide.pt.feature_search.domain.usecase

import cm.aptoide.pt.feature_search.domain.Result
import cm.aptoide.pt.feature_search.domain.model.SearchHistory
import cm.aptoide.pt.feature_search.domain.repository.SearchHistoryRepository
import java.io.IOException

class GetSearchHistoryUseCase(private val searchHistoryRepository: SearchHistoryRepository) {

  suspend fun getSearchHistory(): Result<List<SearchHistory>> {
    return try {
      Result.Success(searchHistoryRepository.getSearchHistory())
    } catch (e: IOException) {
      Result.Error(e)
    }
  }
}