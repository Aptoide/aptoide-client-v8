package cm.aptoide.pt.feature_search.data.database

import androidx.room.Dao
import androidx.room.Query
import cm.aptoide.pt.feature_search.data.database.model.SearchHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchHistoryDao : LocalSearchHistoryRepository {

  @Query("SELECT * from searchHistory")
  override fun getSearchHistory(): Flow<List<SearchHistoryEntity>>

  override suspend fun addAppToSearchHistory(appName: String)
}