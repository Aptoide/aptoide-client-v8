package cm.aptoide.pt.database.room;

import androidx.room.TypeConverter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.List;

public class StringTypeConverter {
  @TypeConverter public static List<String> restoreList(String listOfString) {
    return new Gson().fromJson(listOfString, new TypeToken<List<String>>() {
    }.getType());
  }

  @TypeConverter public static String saveList(List<String> listOfString) {
    return new Gson().toJson(listOfString);
  }
}
