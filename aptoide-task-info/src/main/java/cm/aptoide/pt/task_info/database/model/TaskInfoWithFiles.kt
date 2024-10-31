package cm.aptoide.pt.task_info.database.model

import androidx.room.Embedded
import androidx.room.Relation

data class TaskInfoWithFiles(
  @Embedded val taskInfo: TaskInfoData,
  @Relation(parentColumn = "timestamp", entityColumn = "taskTimestamp")
  val installationFiles: List<InstallationFileData>
)
