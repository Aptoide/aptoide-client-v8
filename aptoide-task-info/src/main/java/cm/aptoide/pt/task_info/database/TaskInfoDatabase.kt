package cm.aptoide.pt.task_info.database

import androidx.room.Database
import androidx.room.RoomDatabase
import cm.aptoide.pt.task_info.database.model.InstallationFileData
import cm.aptoide.pt.task_info.database.model.TaskInfoData

@Database(
  version = 1,
  entities = [TaskInfoData::class, InstallationFileData::class],
)
abstract class TaskInfoDatabase : RoomDatabase() {
  abstract fun taskInfoDao(): TaskInfoDao
  abstract fun installationFileDao(): InstallationFileDao
}
