package cm.aptoide.pt.feature_search.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import cm.aptoide.pt.feature_search.data.database.model.SearchHistoryEntity

@Database(
  entities = [SearchHistoryEntity::class],
  version = 1
)
abstract class SearchHistoryDatabase : RoomDatabase() {

  abstract fun searchDao(): SearchHistoryDao
}