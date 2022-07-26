package cm.aptoide.pt.downloads_database.data.database.model

import androidx.room.TypeConverter
import cm.aptoide.pt.downloads_database.data.database.model.FileToDownload
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object FileToDownloadTypeConverter {
  @TypeConverter
  fun restoreFileToDownloadList(listOfString: String?): List<FileToDownload> {
    return Gson().fromJson(listOfString, object : TypeToken<List<FileToDownload?>?>() {}.type)
  }

  @TypeConverter
  fun saveFileToDownloadList(listOfString: List<FileToDownload?>?): String {
    return Gson().toJson(listOfString)
  }
}