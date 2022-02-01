package cm.aptoide.pt.feature_search.data.database

import cm.aptoide.pt.feature_search.data.database.model.SearchHistoryEntity

interface SearchHistoryDao {

  suspend fun getSearchHistory(): List<SearchHistoryEntity>

  suspend fun addAppToSearchHistory(appName: String)
}