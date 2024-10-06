package cm.aptoide.pt.database.room;

import androidx.room.TypeConverter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.List;

class CampaignUrlTypeConverter {
  @TypeConverter public static List<RoomCampaignUrl> restoreCampaignUrlList(String listOfString) {
    return new Gson().fromJson(listOfString, new TypeToken<List<RoomCampaignUrl>>() {
    }.getType());
  }

  @TypeConverter public static String saveCampaignUrlList(List<RoomCampaignUrl> listOfString) {
    return new Gson().toJson(listOfString);
  }
}
