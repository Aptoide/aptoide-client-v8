package cm.aptoide.pt.database.room;

import androidx.room.TypeConverter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.List;

public class FileToDownloadTypeConverter {

  @TypeConverter
  public static List<RoomFileToDownload> restoreFileToDownloadList(String listOfString) {
    return new Gson().fromJson(listOfString, new TypeToken<List<RoomFileToDownload>>() {
    }.getType());
  }

  @TypeConverter
  public static String saveFileToDownloadList(List<RoomFileToDownload> listOfString) {
    return new Gson().toJson(listOfString);
  }
}
