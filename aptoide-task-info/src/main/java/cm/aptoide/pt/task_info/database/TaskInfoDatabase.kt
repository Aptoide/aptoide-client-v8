package cm.aptoide.pt.task_info.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import cm.aptoide.pt.task_info.database.model.InstallationFileData
import cm.aptoide.pt.task_info.database.model.TaskInfoData

@Database(
  version = 2,
  entities = [TaskInfoData::class, InstallationFileData::class],
)
abstract class TaskInfoDatabase : RoomDatabase() {
  abstract fun taskInfoDao(): TaskInfoDao
  abstract fun installationFileDao(): InstallationFileDao

  class FirstMigration : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
      database.execSQL("ALTER TABLE TaskInfo ADD COLUMN payload TEXT")
    }
  }
}
