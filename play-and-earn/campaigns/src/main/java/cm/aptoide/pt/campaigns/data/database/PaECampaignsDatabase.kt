package cm.aptoide.pt.campaigns.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import cm.aptoide.pt.campaigns.data.database.model.PaEMissionEntity
import com.google.gson.JsonObject
import com.google.gson.JsonParser

@Database(
  entities = [PaEMissionEntity::class],
  version = 1,
  exportSchema = true
)
@TypeConverters(PaeCampaignsConverters::class)
internal abstract class PaECampaignsDatabase : RoomDatabase() {

  abstract fun paeMissionDao(): PaeMissionDao
}

class PaeCampaignsConverters {

  @TypeConverter
  fun jsonObjectToString(value: JsonObject?): String? = value?.toString()

  @TypeConverter
  fun stringToJsonObject(value: String?): JsonObject? =
    value?.let { JsonParser.parseString(it).asJsonObject }
}
