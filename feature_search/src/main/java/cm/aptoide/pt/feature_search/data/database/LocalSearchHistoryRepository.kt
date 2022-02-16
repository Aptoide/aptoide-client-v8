package cm.aptoide.pt.feature_search.data.database

import cm.aptoide.pt.feature_search.data.database.model.SearchHistoryEntity
import kotlinx.coroutines.flow.Flow

interface LocalSearchHistoryRepository {
  fun getSearchHistory(): Flow<List<SearchHistoryEntity>>

  suspend fun addAppToSearchHistory(appName: String)
}