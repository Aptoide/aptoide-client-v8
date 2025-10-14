package cm.aptoide.pt.campaigns.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pae_apps")
internal data class PaEAppEntity(
  @PrimaryKey
  val packageName: String
)
