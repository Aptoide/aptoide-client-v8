package cm.aptoide.pt.feature_search.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [SearchHistoryDatabase::class], version = 1, exportSchema = false)
internal abstract class SearchHistoryDatabase : RoomDatabase() {

  abstract fun searchDao(): SearchHistoryDao
}