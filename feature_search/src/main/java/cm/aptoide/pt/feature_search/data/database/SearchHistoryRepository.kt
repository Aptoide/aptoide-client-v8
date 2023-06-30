package cm.aptoide.pt.feature_search.data.database

import cm.aptoide.pt.feature_search.data.database.model.SearchHistoryEntity
import kotlinx.coroutines.flow.Flow

interface SearchHistoryRepository {
  fun getSearchHistory(): Flow<List<SearchHistoryEntity>>

  fun addAppToSearchHistory(searchHistory: SearchHistoryEntity)

  fun removeAppFromSearchHistory(appName: String)
}
