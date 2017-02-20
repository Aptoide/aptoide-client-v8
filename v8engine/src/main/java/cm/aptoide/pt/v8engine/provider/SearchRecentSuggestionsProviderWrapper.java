package cm.aptoide.pt.v8engine.provider;

import android.content.SearchRecentSuggestionsProvider;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by pedroribeiro on 18/01/17.
 */

public class SearchRecentSuggestionsProviderWrapper extends SearchRecentSuggestionsProvider {

  protected String buildJson(String query) {
    JSONObject jsonObject = new JSONObject();
    try {
      jsonObject.put("query", query);
      jsonObject.put("limit", 5);
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return jsonObject.toString();
  }
}
