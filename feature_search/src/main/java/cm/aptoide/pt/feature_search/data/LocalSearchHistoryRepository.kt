package cm.aptoide.pt.feature_search.data

import cm.aptoide.pt.feature_search.data.database.SearchHistoryDao
import cm.aptoide.pt.feature_search.domain.model.SearchHistory
import cm.aptoide.pt.feature_search.domain.repository.SearchHistoryRepository

class LocalSearchHistoryRepository(private val searchHistoryDao: SearchHistoryDao) :
  SearchHistoryRepository {

  override suspend fun getSearchHistory(): List<SearchHistory> {
    return try {
      searchHistoryDao.getSearchHistory().map { SearchHistory(it.appName) }
    } catch (e: Exception) {
      ArrayList()
    }
  }

  override suspend fun addAppToSearchHistory(appName: String) {
    searchHistoryDao.addAppToSearchHistory(appName)
  }

}