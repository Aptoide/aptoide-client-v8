package cm.aptoide.pt.feature_search.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import cm.aptoide.pt.feature_search.data.database.model.SearchHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchHistoryDao : SearchHistoryRepository {

  @Query("SELECT * from searchHistory")
  override fun getSearchHistory(): Flow<List<SearchHistoryEntity>>

  @Insert(onConflict = REPLACE)
  override fun addAppToSearchHistory(searchHistory: SearchHistoryEntity)

  @Delete
  override fun removeAppFromSearchHistory(searchHistory: SearchHistoryEntity)
}