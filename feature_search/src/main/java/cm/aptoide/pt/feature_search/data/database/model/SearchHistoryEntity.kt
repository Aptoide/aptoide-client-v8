package cm.aptoide.pt.feature_search.data.database.model

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "SearchHistory")
data class SearchHistoryEntity(
  @PrimaryKey @NonNull val appName: String
)