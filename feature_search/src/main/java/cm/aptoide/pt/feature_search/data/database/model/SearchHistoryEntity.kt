package cm.aptoide.pt.feature_search.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "SearchHistory", indices = [Index(value = ["appName"], unique = true)])
data class SearchHistoryEntity(
  @PrimaryKey(autoGenerate = true) val id: Long,
  @ColumnInfo(name = "appName") val appName: String,
) {
  constructor(appName: String) : this(0, appName)
}
