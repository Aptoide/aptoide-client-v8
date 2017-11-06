package cm.aptoide.pt.search.view;

import android.database.Cursor;
import android.support.v7.widget.*;

/**
 * Created by franciscocalado on 11/6/17.
 */

public class WebSocketQueryResultRepository implements QueryResultRepository {

  private final android.support.v7.widget.SearchView searchView;

  public WebSocketQueryResultRepository(android.support.v7.widget.SearchView searchView) {
    this.searchView = searchView;
  }

  @Override public String getQueryAt(int index) {
    Cursor item = (Cursor) searchView.getSuggestionsAdapter()
        .getItem(index);
    return item.getString(1);
  };
}

