package cm.aptoide.pt.campaigns.data.database.model

import androidx.room.Embedded
import androidx.room.Entity
import cm.aptoide.pt.campaigns.domain.PaEMission
import cm.aptoide.pt.campaigns.domain.PaEMissionProgress
import cm.aptoide.pt.campaigns.domain.PaEMissionType
import com.google.gson.JsonObject

@Entity(
  tableName = "pae_missions",
  primaryKeys = ["packageName", "title"],
)
data class PaEMissionEntity(
  val packageName: String,
  val title: String,
  val description: String?,
  val icon: String?,
  val type: PaEMissionType,
  val arguments: JsonObject,
  val units: Int,
  @Embedded(prefix = "progress")
  val progress: PaEMissionProgress?
)

fun PaEMissionEntity.toDomain(): PaEMission = PaEMission(
  title = title,
  description = description,
  icon = icon,
  type = type,
  arguments = arguments,
  units = units,
  progress = progress
)

fun PaEMission.toEntity(packageName: String): PaEMissionEntity = PaEMissionEntity(
  packageName = packageName,
  title = title,
  description = description,
  icon = icon,
  type = type,
  arguments = arguments,
  units = units,
  progress = progress
)
