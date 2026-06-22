package cm.aptoide.pt.campaigns.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

// Cached P&E campaign app. The per-user, volatile progress
// is intentionally not persisted (it must always come from a fresh network fetch).
@Entity(tableName = "pae_apps")
internal data class PaEAppEntity(
  @PrimaryKey
  val packageName: String,
  val icon: String,
  val graphic: String,
  val name: String,
  val uname: String,
  val totalPrizes: Int,
)
