package cm.aptoide.pt.feature_search.domain.repository

import cm.aptoide.pt.feature_search.domain.model.SearchHistory

interface SearchHistoryRepository {
  suspend fun getSearchHistory() : List<SearchHistory>

  suspend fun addAppToSearchHistory(appName: String)
}