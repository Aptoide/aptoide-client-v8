package cm.aptoide.pt.task_info.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import cm.aptoide.pt.install_manager.dto.InstallationFile

@Entity(tableName = "InstallationFile")
data class InstallationFileData(
  @PrimaryKey(autoGenerate = true) val id: Int? = null,
  val packageName: String,
  val name: String,
  val type: InstallationFile.Type,
  val md5: String,
  val fileSize: Long,
  val url: String,
  val altUrl: String,
  val localPath: String
)
