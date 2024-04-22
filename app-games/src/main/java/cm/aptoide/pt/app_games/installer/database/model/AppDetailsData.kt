package cm.aptoide.pt.app_games.installer.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "AppDetails")
data class AppDetailsData(
  @PrimaryKey val packageName: String,
  val name: String?,
  val icon: String?,
)
