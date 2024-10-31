package cm.aptoide.pt.task_info.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import cm.aptoide.pt.install_manager.Task

@Entity(tableName = "TaskInfo")
data class TaskInfoData(
  @PrimaryKey val timestamp: Long,
  val packageName: String,
  val versionCode: Long,
  val versionName: String,
  val constraints: String,
  val type: Task.Type,
  val payload: String?,
)
