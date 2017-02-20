package cm.aptoide.pt.v8engine.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.websocket.StoreAutoCompleteWebSocket;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by pedroribeiro on 24/01/17.
 */

public class StoreAutoCompleteAdapter extends BaseAdapter implements Filterable {

  private static int MAX_SUGGESTIONS = 5;
  private static StoreAutoCompleteWebSocket storeAutoCompleteWebSocket;
  private Context context;
  private List<String> suggestions = new ArrayList<>();

  public StoreAutoCompleteAdapter(Context context,
      StoreAutoCompleteWebSocket storeAutoCompleteWebSocket) {
    this.context = context;
    this.storeAutoCompleteWebSocket = storeAutoCompleteWebSocket;
  }

  public StoreAutoCompleteAdapter(Context context) {
    this.context = context;
  }

  @Override public int getCount() {
    return suggestions.size();
  }

  @Override public Object getItem(int position) {
    return suggestions.get(position);
  }

  @Override public long getItemId(int position) {
    return position;
  }

  @Override public View getView(int position, View view, ViewGroup viewGroup) {
    if (view == null) {
      LayoutInflater inflater =
          (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      view = inflater.inflate(R.layout.add_stores_suggestion_item, viewGroup, false);
    }
    ((TextView) view.findViewById(R.id.text1)).setText(getItem(position).toString());
    return view;
  }

  @Override public Filter getFilter() {
    Filter filter = new Filter() {

      @Override protected FilterResults performFiltering(CharSequence charSequence) {
        FilterResults filterResults = new FilterResults();
        if (charSequence != null) {
          if (storeAutoCompleteWebSocket.getWebSocket() != null) {
            sendQuery(charSequence);
          } else {
            storeAutoCompleteWebSocket.connect("9002");
            sendQuery(charSequence);
          }
          AptoideUtils.ThreadU.runOnUiThread(() -> {
            Log.d("Ribas", Thread.currentThread().toString());
            filterResults.values = storeAutoCompleteWebSocket.getResults();
            filterResults.count = storeAutoCompleteWebSocket.getResults().size();
          });
        }
        return filterResults;
      }

      @Override protected void publishResults(final CharSequence charSequence,
          final FilterResults filterResults) {
        if (filterResults != null && filterResults.count > 0) {
          AptoideUtils.ThreadU.runOnUiThread(() -> {
            suggestions = (List<String>) filterResults.values;
            Log.d("Ribas", Thread.currentThread().toString());
            notifyDataSetChanged();
          });
        } else {
          notifyDataSetInvalidated();
        }
      }
    };
    return filter;
  }

  private void sendQuery(CharSequence charSequence) {
    storeAutoCompleteWebSocket.sendAndReceive(buildJson(charSequence.toString()).toString(), this);
  }

  private String buildJson(String query) {
    JSONObject jsonObj = new JSONObject();
    try {
      jsonObj.put("query", query);
      jsonObj.put("limit", 5);
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return jsonObj.toString();
  }

  public void updateSuggestions(List<String> results) {
    suggestions = results;
  }
}
