package cm.aptoide.pt.task_info.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import cm.aptoide.pt.install_manager.Task

@Entity(tableName = "TaskInfo")
data class TaskInfoData(
  @PrimaryKey val packageName: String,
  val versionCode: Long,
  val versionName: String,
  val downloadSize: Long,
  val type: Task.Type,
  val timestamp: Long,
  val payload: String?,
)
