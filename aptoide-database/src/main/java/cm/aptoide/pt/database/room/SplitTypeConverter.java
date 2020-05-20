package cm.aptoide.pt.database.room;

import androidx.room.TypeConverter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.List;

public class SplitTypeConverter {

  @TypeConverter public static List<RoomSplit> restoreSplitList(String listOfString) {
    return new Gson().fromJson(listOfString, new TypeToken<List<RoomSplit>>() {
    }.getType());
  }

  @TypeConverter public static String saveSplitList(List<RoomSplit> listOfString) {
    return new Gson().toJson(listOfString);
  }
}
