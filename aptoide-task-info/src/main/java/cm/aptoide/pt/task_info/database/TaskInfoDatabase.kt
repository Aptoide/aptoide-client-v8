package cm.aptoide.pt.task_info.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RenameColumn
import androidx.room.RoomDatabase
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import cm.aptoide.pt.task_info.database.model.InstallationFileData
import cm.aptoide.pt.task_info.database.model.TaskInfoData

@Database(
  version = 3,
  exportSchema = true,
  entities = [TaskInfoData::class, InstallationFileData::class],
  autoMigrations = [
    AutoMigration(
      from = 2,
      to = 3,
      spec = TaskInfoDatabase.SecondAutoMigration::class
    )
  ]
)
abstract class TaskInfoDatabase : RoomDatabase() {
  abstract fun taskInfoDao(): TaskInfoDao
  abstract fun installationFileDao(): InstallationFileDao

  class FirstMigration : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
      database.execSQL("ALTER TABLE TaskInfo ADD COLUMN payload TEXT")
    }
  }

  @RenameColumn(
    tableName = "TaskInfo",
    fromColumnName = "downloadSize",
    toColumnName = "constraints"
  )
  class SecondAutoMigration : AutoMigrationSpec {
    override fun onPostMigrate(database: SupportSQLiteDatabase) {
      database.execSQL("UPDATE TaskInfo SET constraints = '' ")
    }
  }
}
