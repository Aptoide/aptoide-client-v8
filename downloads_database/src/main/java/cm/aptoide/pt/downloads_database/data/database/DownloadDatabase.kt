package cm.aptoide.pt.downloads_database.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import cm.aptoide.pt.downloads_database.data.database.model.DownloadEntity

@Database(entities = [DownloadEntity::class], version = 1, exportSchema = false)
abstract class SearchHistoryDatabase : RoomDatabase() {

  abstract fun downloadDao(): DownloadDao
}