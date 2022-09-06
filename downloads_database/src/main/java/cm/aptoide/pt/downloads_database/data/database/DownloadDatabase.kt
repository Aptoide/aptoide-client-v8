package cm.aptoide.pt.downloads_database.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import cm.aptoide.pt.downloads_database.data.database.model.DownloadEntity
import cm.aptoide.pt.downloads_database.data.database.model.FileToDownloadTypeConverter

@Database(entities = [DownloadEntity::class], version = 1, exportSchema = false)
@TypeConverters(
  FileToDownloadTypeConverter::class
)
abstract class DownloadDatabase : RoomDatabase() {

  abstract fun downloadDao(): DownloadDao
}