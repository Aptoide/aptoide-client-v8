package cm.aptoide.analytics.implementation.utils;

import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

public class MapToJsonMapper {

  public JSONObject mapToJsonObject(Map<String, Object> data) {
    JSONObject eventData = new JSONObject();
    for (Map.Entry<String, Object> entry : data.entrySet()) {
      if (entry.getValue() != null) {
        try {
          eventData.put(entry.getKey(), entry.getValue()
              .toString());
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }
    }
    return eventData;
  }
}
